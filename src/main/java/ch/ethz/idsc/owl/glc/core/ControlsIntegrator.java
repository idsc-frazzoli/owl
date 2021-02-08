// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/** private utility class to facilitate the construction of the map */
/* private */ class FlowTrajectory {
  private final Tensor flow;
  private final List<StateTime> trajectory;

  FlowTrajectory(Tensor flow, List<StateTime> trajectory) {
    this.flow = flow;
    this.trajectory = trajectory;
  }

  List<StateTime> trajectory() {
    return trajectory;
  }

  /** @param node origin of trajectory
   * @param costFunction in order to derive costs and minCostToGoal of returned node
   * @return new node with state at end of trajectory */
  GlcNode createGlcNode(GlcNode node, CostFunction costFunction) {
    StateTime last = Lists.getLast(trajectory);
    return GlcNode.of(flow, last, //
        node.costFromRoot().add(costFunction.costIncrement(node, trajectory, flow)), //
        costFunction.minCostToGoal(last.state()));
  }
}

/** utility class used in {@link TrajectoryPlanner}s to compute
 * the trajectories from a given node for all controls.
 * 
 * Since the integration is independent for all controls,
 * the implementation can be carried out in parallel. */
public class ControlsIntegrator implements Serializable {
  private final StateIntegrator stateIntegrator;
  private final StateTimeFlows stateTimeFlows;
  private final CostFunction costFunction;

  /** @param stateIntegrator
   * @param stateTimeFlows of stream of control flows
   * @param costFunction */
  public ControlsIntegrator( //
      StateIntegrator stateIntegrator, StateTimeFlows stateTimeFlows, CostFunction costFunction) {
    this.stateIntegrator = Objects.requireNonNull(stateIntegrator);
    this.stateTimeFlows = Objects.requireNonNull(stateTimeFlows);
    this.costFunction = Objects.requireNonNull(costFunction);
  }

  /** parallel trajectory integration is used by {@link StandardTrajectoryPlanner}
   * 
   * @param node from which to expand
   * @return */
  public Map<GlcNode, List<StateTime>> from(GlcNode node) {
    StateTime stateTime = node.stateTime();
    return stateTimeFlows.flows(stateTime).parallelStream() // parallel stream results in speedup of ~25% (rice2demo)
        .map(flow -> new FlowTrajectory(flow, stateIntegrator.trajectory(node.stateTime(), flow))) //
        .collect(Collectors.toMap( //
            flowTrajectory -> flowTrajectory.createGlcNode(node, costFunction), //
            FlowTrajectory::trajectory));
  }
}
