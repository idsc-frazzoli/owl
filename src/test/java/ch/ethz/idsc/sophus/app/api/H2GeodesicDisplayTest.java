// code by jph
package ch.ethz.idsc.sophus.app.api;

import junit.framework.TestCase;

public class H2GeodesicDisplayTest extends TestCase {
  public void testFail() {
    assertNull(HP2GeodesicDisplay.INSTANCE.lieGroup());
    assertNull(HP2GeodesicDisplay.INSTANCE.exponential());
  }
}
