// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.owl.bot.r2.R2ExamplePolygons;
import junit.framework.TestCase;

public class PolygonRegionTest extends TestCase {
  public void testPolygon() {
    PolygonRegion polygonRegion = (PolygonRegion) PolygonRegions.numeric(R2ExamplePolygons.BULKY_TOP_LEFT);
    assertEquals(polygonRegion.polygon(), R2ExamplePolygons.BULKY_TOP_LEFT);
  }
}
