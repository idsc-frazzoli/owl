// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class VectorCostGoalAdapter implements GoalInterface, Serializable {
  private final List<CostFunction> costFunctions;
  private final TrajectoryRegionQuery trajectoryRegionQuery;

  public VectorCostGoalAdapter( //
      List<CostFunction> costFunctions, //
      Se2ComboRegion se2ComboRegion, //
      Collection<Flow> controls) {
    this.costFunctions = costFunctions;
    this.trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(se2ComboRegion);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return VectorScalar.of(costFunctions.stream() //
        .map(costFunction -> costFunction.minCostToGoal(x)));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return VectorScalar.of(costFunctions.stream() //
        .map(costFunction -> costFunction.costIncrement(glcNode, trajectory, flow)));
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectoryRegionQuery.firstMember(trajectory);
  }

  @Override // from TrajectoryRegionQuery
  public boolean isMember(StateTime stateTime) {
    return trajectoryRegionQuery.isMember(stateTime);
  }
}
