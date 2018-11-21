// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BartlettWindowTest extends TestCase {
  public void testZero() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testExact() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RationalScalar.of(3, 3465));
    assertTrue(ExactScalarQ.of(scalar));
    assertEquals(scalar, RationalScalar.of(1153, 1155));
  }

  public void testContinuous() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.of(.499999999));
    assertTrue(Chop._07.allZero(scalar));
  }
}
