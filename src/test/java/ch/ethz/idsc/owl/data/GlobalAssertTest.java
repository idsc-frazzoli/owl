// code by jph
package ch.ethz.idsc.owl.data;

import junit.framework.TestCase;

public class GlobalAssertTest extends TestCase {
  public void testSimple() {
    try {
      GlobalAssert.that(false);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
