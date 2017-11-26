// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import junit.framework.TestCase;

public class R2dControlsTest extends TestCase {
  public void testSimple() {
    assertEquals(R2dControls.createRadial(3).size(), 9);
  }

  public void testFail() {
    try {
      R2dControls.createRadial(2);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
