// code by jph
package ch.ethz.idsc.sophus.app.api;

import junit.framework.TestCase;

public class H2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertNull(H2GeodesicDisplay.INSTANCE.lieGroup());
  }
}
