// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So2GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor p = So2Exponential.INSTANCE.exp(RealScalar.ONE);
    Tensor q = So2Exponential.INSTANCE.exp(RealScalar.of(2));
    Scalar split = So2Geodesic.INSTANCE.split(p, q, RationalScalar.HALF);
    assertEquals(split, RationalScalar.of(3, 2));
    ExactScalarQ.require(split);
  }

  public void testEndPoints() {
    Distribution distribution = UniformDistribution.unit();
    for (int index = 0; index < 10; ++index) {
      Tensor p = So2Exponential.INSTANCE.exp(RandomVariate.of(distribution));
      Tensor q = So2Exponential.INSTANCE.exp(RandomVariate.of(distribution));
      Chop._12.requireClose(p, So2Geodesic.INSTANCE.split(p, q, RealScalar.ZERO));
      Chop._12.requireClose(q, So2Geodesic.INSTANCE.split(p, q, RealScalar.ONE));
    }
  }
}
