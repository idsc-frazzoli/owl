// code by jph
package ch.ethz.idsc.sophus.lie.he;

import junit.framework.TestCase;

public class HeGroupTest extends TestCase {
  public void testSimple() {
    try {
      HeGroup.INSTANCE.element(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
