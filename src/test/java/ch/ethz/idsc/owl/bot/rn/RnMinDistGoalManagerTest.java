// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RnMinDistGoalManagerTest extends TestCase {
  public void testSimple() {
    GoalInterface goalInterface = RnMinDistGoalManager.sperical(Tensors.vector(10, 10, 10), RealScalar.of(2));
    Scalar scalar = goalInterface.minCostToGoal(Tensors.vector(10, 0, 10));
    assertEquals(scalar, RealScalar.of(8));
  }

  public void testQuantity() {
    GoalInterface goalInterface = RnMinDistGoalManager.sperical(Tensors.fromString("{10[m], 10[m], 10[m]}"), Quantity.of(2, "m"));
    Scalar scalar = goalInterface.minCostToGoal(Tensors.fromString("{10[m],0[m],10[m]}"));
    assertEquals(scalar, Quantity.of(8, "m"));
  }
}
