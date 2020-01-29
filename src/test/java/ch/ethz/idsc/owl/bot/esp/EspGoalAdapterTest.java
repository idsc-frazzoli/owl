// code by jph
package ch.ethz.idsc.owl.bot.esp;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class EspGoalAdapterTest extends TestCase {
  public void testGoal() {
    Tensor goal = EspGoalAdapter.GOAL;
    assertEquals(EspGoalAdapter.INSTANCE.minCostToGoal(goal), RealScalar.ZERO);
    assertFalse(EspObstacleRegion.INSTANCE.isMember(goal));
  }

  public void testStart() {
    assertTrue(Scalars.lessEquals( //
        RealScalar.of(0), //
        EspGoalAdapter.INSTANCE.minCostToGoal(EspDemo.START)));
  }
}
