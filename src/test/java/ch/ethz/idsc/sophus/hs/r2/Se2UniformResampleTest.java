// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import junit.framework.TestCase;

public class Se2UniformResampleTest extends TestCase {
  public void testSimple() {
    try {
      Se2UniformResample.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
