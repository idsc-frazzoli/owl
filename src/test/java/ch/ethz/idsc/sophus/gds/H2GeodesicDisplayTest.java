// code by jph
package ch.ethz.idsc.sophus.gds;

import junit.framework.TestCase;

public class H2GeodesicDisplayTest extends TestCase {
  public void testFail() {
    assertNull(HP2GeodesicDisplay.INSTANCE.lieGroup());
  }
}
