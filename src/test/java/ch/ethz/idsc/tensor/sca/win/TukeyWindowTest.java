// code by jph
package ch.ethz.idsc.tensor.sca.win;

import junit.framework.TestCase;

public class TukeyWindowTest extends TestCase {
  public void testIsZeroTukey() {
    assertTrue(TukeyWindow.function().isZero());
  }
}
