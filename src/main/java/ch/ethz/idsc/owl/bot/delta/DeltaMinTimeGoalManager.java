// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/** heuristic adds max speed of available control to max norm of image gradient */
/* package */ class DeltaMinTimeGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  // ---
  private final RegionWithDistance<Tensor> regionWithDistance;
  /** unit of maxSpeed is velocity, e.g. [m/s] */
  private final Scalar maxSpeed;

  /** @param regionWithDistance
   * @param maxSpeed positive */
  public DeltaMinTimeGoalManager(RegionWithDistance<Tensor> regionWithDistance, Scalar maxSpeed) {
    super(new TimeInvariantRegion(regionWithDistance));
    this.regionWithDistance = regionWithDistance;
    this.maxSpeed = Sign.requirePositive(maxSpeed);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory); // unit [s]
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return regionWithDistance.distance(x).divide(maxSpeed); // unit [m] / [m/s] simplifies to [s]
  }
}
