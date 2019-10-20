// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class GeodesicCatmullRomDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicCatmullRomDemo());
  }
}
