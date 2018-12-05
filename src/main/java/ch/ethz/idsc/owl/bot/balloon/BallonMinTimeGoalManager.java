// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
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

  public static void main(String[] args) {
    Tensor goal = Tensors.vector(3, 3, 3, 3);
    Scalar maxSpeed = Quantity.of(10, "m*s^-1");
    Scalar goalRadius = RealScalar.of(1);
    System.out.println(goal.extract(0, 2));
    SphericalRegion sphericalRegion = new SphericalRegion(goal.extract(0, 2), goalRadius);
    BalloonMinTimeGoalManager balloonMinTimeGoalManager = new BalloonMinTimeGoalManager(sphericalRegion, maxSpeed);
    Scalar expected = Quantity.of(4, "s").divide(maxSpeed);
    System.out.println(expected);
    System.out.println(balloonMinTimeGoalManager.minCostToGoal(Tensors.fromString("{0[m],0[m],0[m*s^-1],0.05[m * K^-1 * s^-2]}")));
  }
}
