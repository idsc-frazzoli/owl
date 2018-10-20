// code by jph
package ch.ethz.idsc.tensor.sca.win;

import junit.framework.TestCase;

public class NuttallWindowTest extends TestCase {
  public void testIsZero() {
    assertTrue(NuttallWindow.function().isZero());
  }
}
