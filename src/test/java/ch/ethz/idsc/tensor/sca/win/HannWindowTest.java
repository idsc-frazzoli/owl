// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import junit.framework.TestCase;

public class HannWindowTest extends TestCase {
  public void testExact() {
    assertEquals(HannWindow.function().apply(RationalScalar.of(+1, 3)), RationalScalar.of(1, 4));
    assertEquals(HannWindow.function().apply(RationalScalar.of(+1, 4)), RationalScalar.of(1, 2));
    assertEquals(HannWindow.function().apply(RationalScalar.of(+1, 6)), RationalScalar.of(3, 4));
    assertEquals(HannWindow.function().apply(RationalScalar.of(-1, 3)), RationalScalar.of(1, 4));
    assertEquals(HannWindow.function().apply(RationalScalar.of(-1, 4)), RationalScalar.of(1, 2));
    assertEquals(HannWindow.function().apply(RationalScalar.of(-1, 6)), RationalScalar.of(3, 4));
  }

  public void testIsZero() {
    assertTrue(HannWindow.function().isContinuous());
  }
}
