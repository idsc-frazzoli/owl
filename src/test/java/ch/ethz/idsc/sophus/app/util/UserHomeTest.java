// code by jph
package ch.ethz.idsc.sophus.app.util;

import junit.framework.TestCase;

public class UserHomeTest extends TestCase {
  public void testSimple() {
    assertTrue(UserHome.file("").isDirectory());
  }

  public void testNull() {
    try {
      UserHome.file(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
