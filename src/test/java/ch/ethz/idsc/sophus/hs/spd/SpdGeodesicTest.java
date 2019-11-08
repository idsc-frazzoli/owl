// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SpdGeodesicTest extends TestCase {
  public void testSimple() {
    for (int n = 1; n < 5; ++n) {
      Tensor p = TestHelper.generateSpd(n);
      Tensor q = TestHelper.generateSpd(n);
      Scalar t = RandomVariate.of(UniformDistribution.unit());
      Tensor m1 = SpdGeodesic.INSTANCE.split(p, q, t);
      Tensor m2 = SpdGeodesic.INSTANCE.split(q, p, RealScalar.ONE.subtract(t));
      Chop._08.requireClose(m1, m2);
    }
  }

  public void testIdentity() {
    for (int n = 1; n < 5; ++n) {
      Tensor p = TestHelper.generateSpd(n);
      Scalar t = RandomVariate.of(UniformDistribution.unit());
      Tensor m = SpdGeodesic.INSTANCE.split(p, p, t);
      Chop._08.requireClose(m, p);
    }
  }
}
