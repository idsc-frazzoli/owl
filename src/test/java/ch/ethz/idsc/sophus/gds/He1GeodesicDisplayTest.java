// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.lie.he.HeGeodesic;
import junit.framework.TestCase;

public class He1GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(He1Display.INSTANCE.geodesicInterface(), HeGeodesic.INSTANCE);
  }
}
