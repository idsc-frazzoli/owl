// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Factorial;
import junit.framework.TestCase;

public class CarDiscreteModelTest extends TestCase {
  public void testStateCount() {
    for (int n = 2; n < 7; ++n) {
      CarDiscreteModel cdm = new CarDiscreteModel(n, 2);
      Tensor states = cdm.states();
      assertEquals(states.length(), Scalars.intValueExact(Factorial.of(n).multiply(RationalScalar.HALF)) + 1);
    }
  }

  public void testZeroFail() {
    AssertFail.of(() -> new CarDiscreteModel(0, 2));
  }
}
