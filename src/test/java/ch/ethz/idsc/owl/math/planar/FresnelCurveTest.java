// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.owl.math.Fresnel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class FresnelCurveTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(Fresnel.domain());
    for (int count = 0; count < 100; ++count) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor p = FresnelCurve.FUNCTION.apply(scalar);
      Tensor q = FresnelCurve.FUNCTION.apply(scalar.negate());
      VectorQ.requireLength(p, 2);
      assertEquals(p, q.negate());
    }
  }
}
