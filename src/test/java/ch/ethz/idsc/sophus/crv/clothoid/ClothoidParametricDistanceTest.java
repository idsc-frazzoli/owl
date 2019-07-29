// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidParametricDistanceTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(2, 10);
    Distribution ad = NormalDistribution.standard();
    int fails = 0;
    for (int count = 0; count < 100; ++count) {
      Tensor q = RandomVariate.of(distribution, 2).append(RandomVariate.of(ad));
      Tensor p = q.map(Scalar::zero);
      Scalar d1 = ClothoidParametricDistance.INSTANCE.distance(p, q);
      Scalar d2 = PseudoClothoidDistance.INSTANCE.distance(p, q);
      boolean close = Chop._01.close(d1, d2);
      if (!close)
        ++fails;
    }
    assertTrue(fails < 40);
  }
}
