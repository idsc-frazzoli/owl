// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicBSplineFunctionTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    int n = 20;
    Tensor domain = Subdivide.of(0, n - 1, 100);
    for (int degree = 1; degree < 10; ++degree) {
      Tensor control = RandomVariate.of(distribution, n, 3);
      GeodesicBSplineFunction mapForward = //
          GeodesicBSplineFunction.of(Se2CoveringGeodesic.INSTANCE, degree, control);
      Tensor forward = domain.map(mapForward);
      GeodesicBSplineFunction mapReverse = //
          GeodesicBSplineFunction.of(Se2CoveringGeodesic.INSTANCE, degree, Reverse.of(control));
      Tensor reverse = Reverse.of(domain.map(mapReverse));
      assertTrue(Chop._10.close(forward, reverse));
    }
  }
}
