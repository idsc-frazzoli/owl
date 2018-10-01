// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.red.Mean;
import junit.framework.TestCase;

public class GeodesicMeanFilterTest extends TestCase {
  public void testSimple() {
    for (int radius = 1; radius <= 5; ++radius) {
      GeodesicMeanFilter geodesicMeanFilter = new GeodesicMeanFilter(RnGeodesic.INSTANCE, radius);
      Tensor input = Range.of(0, 2 * radius + 1);
      assertEquals(geodesicMeanFilter.single(input), Mean.of(input));
    }
  }
}
