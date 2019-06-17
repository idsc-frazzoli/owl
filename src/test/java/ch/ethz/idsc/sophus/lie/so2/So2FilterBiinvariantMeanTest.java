// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So2FilterBiinvariantMeanTest extends TestCase {
  public void testLength2Permutations() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    Distribution wd = UniformDistribution.of(-3, 3);
    for (int count = 0; count < 100; ++count) {
      Tensor sequence = RandomVariate.of(distribution, 2);
      Scalar w = RandomVariate.of(wd);
      Tensor weights = Tensors.of(RealScalar.ONE.subtract(w), w);
      Scalar mean1 = So2FilterBiinvariantMean.INSTANCE.mean(sequence, weights);
      Scalar mean2 = So2FilterBiinvariantMean.INSTANCE.mean(Reverse.of(sequence), Reverse.of(weights));
      Chop._13.requireClose(mean1, mean2);
    }
  }
}
