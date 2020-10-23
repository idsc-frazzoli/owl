// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class PolygonCoordinatesDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new PolygonCoordinatesDemo());
  }
}
