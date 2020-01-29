// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;

/** private utility class to facilitate the construction of the map */
/* private */ class FlowTrajectory {
  private final Flow flow;
  private final List<StateTime> trajectory;

  FlowTrajectory(Flow flow, List<StateTime> trajectory) {
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
  private final Supplier<Stream<Flow>> supplier;
  private final CostFunction costFunction;

  /** @param stateIntegrator
   * @param supplier of stream of control {@link Flow}s
   * @param costFunction */
  public ControlsIntegrator( //
      StateIntegrator stateIntegrator, Supplier<Stream<Flow>> supplier, CostFunction costFunction) {
    this.stateIntegrator = Objects.requireNonNull(stateIntegrator);
    this.supplier = supplier;
    this.costFunction = Objects.requireNonNull(costFunction);
  }

  /** parallel trajectory integration is used by {@link StandardTrajectoryPlanner}
   * 
   * @param node from which to expand
   * @return */
  public Map<GlcNode, List<StateTime>> from(GlcNode node) {
    // TODO supply flows depending on state
    return supplier.get() // parallel stream results in speedup of ~25% (rice2demo)
        .map(flow -> new FlowTrajectory(flow, stateIntegrator.trajectory(node.stateTime(), flow))) //
        .collect(Collectors.toMap( //
            flowTrajectory -> flowTrajectory.createGlcNode(node, costFunction), //
            FlowTrajectory::trajectory));
  }
}
