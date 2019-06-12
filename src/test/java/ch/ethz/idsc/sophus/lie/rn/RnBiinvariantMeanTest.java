// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class RnBiinvariantMeanTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);

  public void testSimple() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(3));
    int length = 10;
    Tensor sequence = RandomVariate.of(distribution, length, 3);
    Tensor weights = NORMALIZE.apply(RandomVariate.of(distribution, length));
    Tensor mean = RnBiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._14.requireClose(mean, weights.dot(sequence));
  }

  public void testExact() {
    Distribution distribution = DiscreteUniformDistribution.of(10, 100);
    int length = 10;
    Tensor sequence = RandomVariate.of(distribution, length, 3);
    Tensor weights = NORMALIZE.apply(RandomVariate.of(distribution, length));
    Tensor mean = RnBiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._14.requireClose(mean, weights.dot(sequence));
    ExactTensorQ.require(mean);
  }

  public void testFail() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(3));
    int length = 10;
    Tensor sequence = RandomVariate.of(distribution, length, 3);
    try {
      RnBiinvariantMean.INSTANCE.mean(sequence, Array.of(l -> RealScalar.ONE, length));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
