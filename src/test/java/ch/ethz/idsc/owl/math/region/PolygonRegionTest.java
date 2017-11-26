// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.owl.bot.r2.R2ExamplePolygons;
import junit.framework.TestCase;

public class PolygonRegionTest extends TestCase {
  public void testUnmodif() {
    PolygonRegion pr = new PolygonRegion(R2ExamplePolygons.BULKY_TOP_LEFT);
    assertEquals(pr.polygon(), R2ExamplePolygons.BULKY_TOP_LEFT);
  }
}
