package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class BalloonMinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    Tensor goal = Tensors.fromString("{5[m],0[m],50[m*s^-1],40[m * K^-1 * s^-2]}");
    Scalar maxSpeed = Quantity.of(10, "m*s^-1");
    Scalar goalRadius = Quantity.of(1, "m");
    // GOAL
    SphericalRegion sphericalRegion = new SphericalRegion(goal.extract(0, 2), goalRadius);
    BalloonMinTimeGoalManager balloonMinTimeGoalManager = //
        new BalloonMinTimeGoalManager(Tensors.vector(1, 2), RealScalar.ONE, maxSpeed);
    Scalar expected = Quantity.of(4, "m").divide(maxSpeed);
    // input here state x
    // TODO ASTOLL
    // assertEquals(expected, balloonMinTimeGoalManager.minCostToGoal(Tensors.fromString("{0[m],0[m],0[m*s^-1],0.05[m * K^-1 * s^-2]}")));
  }
}
