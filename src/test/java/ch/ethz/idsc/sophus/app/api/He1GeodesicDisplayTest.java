// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.HeGeodesic;
import junit.framework.TestCase;

public class He1GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(He1GeodesicDisplay.INSTANCE.geodesicInterface(), HeGeodesic.INSTANCE);
  }
}
