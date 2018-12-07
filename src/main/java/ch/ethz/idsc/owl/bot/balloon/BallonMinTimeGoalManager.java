// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/** heuristic adds max speed of available control to max norm of image gradient */
/* package */ class BalloonMinTimeGoalManager extends AbstractMinTimeGoalManager {
  private final RegionWithDistance<Tensor> regionWithDistance;
  /** unit of maxSpeed is velocity, e.g. [m/s] */
  private final Scalar maxSpeed;

  /** @param regionWithDistance
   * @param maxSpeed positive */
  public BalloonMinTimeGoalManager(RegionWithDistance<Tensor> regionWithDistance, Scalar maxSpeed) {
    super(regionWithDistance);
    this.regionWithDistance = regionWithDistance;
    this.maxSpeed = Sign.requirePositive(maxSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return regionWithDistance.distance(x.extract(0, 2)).divide(maxSpeed); // unit [m] / [m/s] simplifies to [s]
  }
}
