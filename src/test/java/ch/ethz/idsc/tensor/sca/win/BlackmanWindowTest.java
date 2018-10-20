// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BlackmanWindowTest extends TestCase {
  public void testSimple() {
    Scalar result = BlackmanWindow.function().apply(RealScalar.of(.2));
    Scalar expect = RealScalar.of(0.50978713763747791812); // checked with Mathematica
    assertTrue(Chop._12.close(result, expect));
  }

  public void testFail() {
    assertEquals(BlackmanWindow.function().apply(RealScalar.of(-.51)), RealScalar.ZERO);
  }

  public void testIsZero() {
    assertTrue(BlackmanWindow.function().isZero());
  }
}
