// code by jph
package ch.ethz.idsc.owl.math;

import java.awt.Color;

import junit.framework.TestCase;

public class HsluvTest extends TestCase {
  public void testNegative() {
    Color color1 = Hsluv.of(-0.1, 1, 1, 1);
    Color color2 = Hsluv.of(+1.1, 1, 1, 1);
    assertEquals(color1, color2);
  }

  public void testFail() {
    try {
      Hsluv.of(1 / 0.0, 1, 1, 1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
