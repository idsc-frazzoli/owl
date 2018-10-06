// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import junit.framework.TestCase;

public class GeodesicCenterFilterTest extends TestCase {
  public void testSimple() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, BinomialMask.FUNCTION);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 3);
    Tensor apply = geodesicCenterFilter.apply(Range.of(0, 10));
    assertEquals(apply, Range.of(0, 10));
  }
}
