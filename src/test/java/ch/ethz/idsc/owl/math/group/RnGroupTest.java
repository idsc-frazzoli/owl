// code by jph
package ch.ethz.idsc.owl.math.group;

import junit.framework.TestCase;

public class RnGroupTest extends TestCase {
  public void testSimple() {
    try {
      RnGroup.INSTANCE.element(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
