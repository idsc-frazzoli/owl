// code by jph
package ch.ethz.idsc.owl.math;

import junit.framework.TestCase;

public class HsluvTest extends TestCase {
  public void testFail() {
    try {
      Hsluv.of(1 / 0.0, 1, 1, 1);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
