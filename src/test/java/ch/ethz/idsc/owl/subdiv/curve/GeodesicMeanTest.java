// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.red.Mean;
import junit.framework.TestCase;

public class GeodesicMeanTest extends TestCase {
  public void testSimple() {
    for (int radius = 0; radius <= 5; ++radius) {
      GeodesicMean geodesicMean = new GeodesicMean(RnGeodesic.INSTANCE);
      Tensor input = Range.of(0, 2 * radius + 1);
      Tensor apply = geodesicMean.apply(input);
      assertEquals(apply, Mean.of(input));
    }
  }
}
