// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class S2GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor p = UnitVector.of(3, 0);
    Tensor q = UnitVector.of(3, 1);
    Tensor split = S2Geodesic.INSTANCE.split(p, q, RationalScalar.HALF);
    assertEquals(Norm._2.of(split), RealScalar.ONE);
    assertEquals(split.Get(0), split.Get(1));
    assertTrue(Scalars.isZero(split.Get(2)));
  }

  public void testEndPoints() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor p = Normalize.of(RandomVariate.of(distribution, 3));
      Tensor q = Normalize.of(RandomVariate.of(distribution, 3));
      assertTrue(Chop._14.close(p, S2Geodesic.INSTANCE.split(p, q, RealScalar.ZERO)));
      Tensor r = S2Geodesic.INSTANCE.split(p, q, RealScalar.ONE);
      assertTrue(Chop._12.close(q, r));
      assertTrue(Chop._14.close(Norm._2.of(r), RealScalar.ONE));
    }
  }
}
