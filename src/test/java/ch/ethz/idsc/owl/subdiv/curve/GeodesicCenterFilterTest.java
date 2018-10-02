// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class GeodesicCenterFilterTest extends TestCase {
  private static Tensor binomial(int i) {
    int width = 2 * i + 1;
    Tensor s = Tensors.vector(k -> Binomial.of(2 * i, k), width);
    return s.divide(Total.of(s).Get());
  }

  public void testSimple() {
    GeodesicCenter geodesicCenter = new GeodesicCenter(RnGeodesic.INSTANCE, GeodesicCenterFilterTest::binomial);
    GeodesicCenterFilter geodesicCenterFilter = new GeodesicCenterFilter(geodesicCenter, 3);
    Tensor apply = geodesicCenterFilter.apply(Range.of(0, 10));
    assertEquals(apply, Range.of(0, 10));
  }
}
