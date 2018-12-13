// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class BalloonMinTimeGoalManagerTest extends TestCase {
  public void testWithoutUnits() {
    Tensor goal = Tensors.vector(0, 0);
    Scalar goalRadius = RealScalar.of(1);
    Scalar maxSpeed = RealScalar.of(10);
    BalloonMinTimeGoalManager balloonMinTimeGoalManager = //
        new BalloonMinTimeGoalManager(goal, goalRadius, maxSpeed);
    Scalar expected = RealScalar.of(4).divide(maxSpeed);
    assertEquals(expected, balloonMinTimeGoalManager.minCostToGoal(Tensors.vector(3, 4, 3, 4)));
    Tensor element = Tensors.vector(0.1, 0, 3, 4);
    assertTrue(balloonMinTimeGoalManager.isMember(element));
  }

  public void testWithUnits() {
    Tensor goal = Tensors.fromString("{5[m],0[m]}");
    Scalar maxSpeed = Quantity.of(10, "m*s^-1");
    Scalar goalRadius = Quantity.of(1, "m");
    BalloonMinTimeGoalManager balloonMinTimeGoalManager = //
        new BalloonMinTimeGoalManager(goal, goalRadius, maxSpeed);
    Scalar expected = Quantity.of(0.4, "s");
    assertEquals(expected, balloonMinTimeGoalManager.minCostToGoal(Tensors.fromString("{8[m],4[m],0[m*s^-1],0.05[m * K^-1 * s^-2]}")));
    Tensor element = Tensors.fromString("{5.5[m],0.1[m],0[m*s^-1],0.05[m * K^-1 * s^-2]}");
    assertTrue(balloonMinTimeGoalManager.isMember(element));
  }
}
