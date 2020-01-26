// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class KlotskiGoalAdapterTest extends TestCase {
  public void testSimple() {
    KlotskiGoalAdapter huarongGoalAdapter = new KlotskiGoalAdapter(Tensors.vector(0, 4, 2));
    for (Huarong huarong : Huarong.values()) {
      Scalar minCostToGoal = huarongGoalAdapter.minCostToGoal(huarong.getBoard());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }
}
