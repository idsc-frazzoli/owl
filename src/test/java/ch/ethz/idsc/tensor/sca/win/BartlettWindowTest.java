// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BartlettWindowTest extends TestCase {
  public void testZero() {
    Scalar scalar = BartlettWindow.function().apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testContinuous() {
    Scalar scalar = BartlettWindow.function().apply(RealScalar.of(.499999999));
    assertTrue(Chop._07.allZero(scalar));
  }

  public void testIsZero() {
    assertTrue(BartlettWindow.function().isZero());
  }
}
