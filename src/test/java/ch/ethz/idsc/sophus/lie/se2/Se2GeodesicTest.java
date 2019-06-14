// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor split = Se2Geodesic.INSTANCE.split(Tensors.vector(0, 0, 0), Tensors.vector(10, 0, 1), RealScalar.of(.7));
    Chop._13.requireClose(split, Tensors.fromString("{7.071951896570488, -1.0688209919859546, 0.7}"));
  }

  public void testBiinvariantMean() {
    Distribution distribution = UniformDistribution.of(-10, 8);
    Distribution wd = UniformDistribution.unit();
    for (int count = 0; count < 10; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Scalar w = RandomVariate.of(wd);
      Tensor mean = Se2BiinvariantMean.FILTER.mean(Tensors.of(p, q), Tensors.of(RealScalar.ONE.subtract(w), w));
      Tensor splt = Se2Geodesic.INSTANCE.split(p, q, w);
      splt.set(So2.MOD, 2);
      Chop._12.requireClose(mean, splt);
    }
  }
}
