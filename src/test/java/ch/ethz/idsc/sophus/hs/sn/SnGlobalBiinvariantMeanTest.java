// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SnGlobalBiinvariantMeanTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    for (int dim = 2; dim < 5; ++dim)
      for (int length = 1; length < 10; ++length) {
        Tensor sequence = Tensor.of(RandomVariate.of(distribution, length, dim).stream().map(NORMALIZE));
        Tensor mean = SnGlobalBiinvariantMean.INSTANCE.mean(sequence, RandomVariate.of(distribution, length));
        Chop._10.requireClose(mean, NORMALIZE.apply(mean));
      }
  }
}
