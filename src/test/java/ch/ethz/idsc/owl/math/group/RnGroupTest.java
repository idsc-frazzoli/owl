// code by jph
package ch.ethz.idsc.owl.math.group;

import junit.framework.TestCase;

public class RnGroupTest extends TestCase {
  public void testSimple() {
    try {
      RnGroup.INSTANCE.element(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
