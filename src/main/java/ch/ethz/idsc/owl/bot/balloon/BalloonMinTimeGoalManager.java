// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.GoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/** heuristic computes minimum time to goal
 * e.g. shortest path to goal (euclidean distance) travelled with maximum speed */
/* package */ class BalloonMinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
  // ---
  private final BallRegion ballRegion;
  /** unit of maxSpeed is velocity, e.g. [m/s] */
  private final Scalar maxSpeed;

  /** @param goal for vectors of length 2
   * @param radius
   * @param maxSpeed positive */
  public BalloonMinTimeGoalManager(Tensor goal, Scalar radius, Scalar maxSpeed) {
    ballRegion = new BallRegion(goal, radius);
    this.maxSpeed = Sign.requirePositive(maxSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return ballRegion.distance(Extract2D.FUNCTION.apply(x)).divide(maxSpeed); // unit [m] / [m/s] simplifies to [s]
  }

  @Override // from Region
  public boolean isMember(Tensor element) {
    return ballRegion.isMember(Extract2D.FUNCTION.apply(element));
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory); // unit [s]
  }

  public GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
