// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class HuarongGoalAdapterTest extends TestCase {
  public void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      Scalar minCostToGoal = HuarongGoalAdapter.INSTANCE.minCostToGoal(huarong.getBoard());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }
}
