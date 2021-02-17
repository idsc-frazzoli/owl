// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import junit.framework.TestCase;

public class R2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(R2Display.INSTANCE.geodesicInterface(), RnGeodesic.INSTANCE);
  }
}
