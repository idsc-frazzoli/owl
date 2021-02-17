// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector1Norm;

/* package */ class KlotskiGoalAdapter implements GoalInterface {
  private final KlotskiGoalRegion klotskiGoalRegion;
  private final Tensor goal_xy;

  /** Example: for Huarong Tensors.vector(0, 4, 2)
   * 
   * @param stone */
  public KlotskiGoalAdapter(Tensor stone) {
    klotskiGoalRegion = new KlotskiGoalRegion(stone);
    this.goal_xy = stone.extract(1, 3);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return Vector1Norm.between(x.get(0).extract(1, 3), goal_xy);
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return RealScalar.ONE;
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectory.stream().filter(this::isMember).findFirst();
  }

  @Override
  public boolean isMember(StateTime x) {
    return klotskiGoalRegion.isMember(x.state());
  }
}
