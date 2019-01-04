// code by jph
package ch.ethz.idsc.sophus.app.api;

import junit.framework.TestCase;

public class GeodesicDisplaysTest extends TestCase {
  public void testSimple() {
    assertTrue(4 <= GeodesicDisplays.ALL.size());
  }
}
