// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** class bundles the capabilities of a given
 * cost function and trajectory region query */
public final class GoalAdapter implements GoalInterface, Serializable {
  private final TrajectoryRegionQuery trajectoryRegionQuery;
  private final CostFunction costFunction;

  public GoalAdapter(TrajectoryRegionQuery trajectoryRegionQuery, CostFunction costFunction) {
    this.trajectoryRegionQuery = trajectoryRegionQuery;
    this.costFunction = costFunction;
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectoryRegionQuery.firstMember(trajectory);
  }

  @Override // from TrajectoryRegionQuery
  public boolean isMember(StateTime stateTime) {
    return trajectoryRegionQuery.isMember(stateTime);
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode node, List<StateTime> trajectory, Tensor flow) {
    return costFunction.costIncrement(node, trajectory, flow);
  }

  @Override // from CostFunction
  public Scalar minCostToGoal(Tensor x) {
    return costFunction.minCostToGoal(x);
  }
}
