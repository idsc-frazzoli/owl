// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class HammingWindowTest extends TestCase {
  public void testSimple() {
    Scalar result = HammingWindow.function().apply(RealScalar.of(.2));
    Scalar expect = RealScalar.of(0.68455123656247599796); // checked with Mathematica
    assertTrue(Chop._12.close(result, expect));
  }

  public void testIsZero() {
    assertFalse(HammingWindow.function().isZero());
  }
}
