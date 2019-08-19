// code by jph
package ch.ethz.idsc.sophus.flt.bm;

import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.sophus.flt.ga.BinomialWeights;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BiinvariantMeanCenterTest extends TestCase {
  public void testSe2() throws ClassNotFoundException, IOException {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          Serialization.copy(BiinvariantMeanCenter.of(Se2BiinvariantMean.GLOBAL, smoothingKernel));
      Distribution distribution = UniformDistribution.unit();
      for (int count = 1; count < 10; ++count) {
        Tensor sequence = RandomVariate.of(distribution, count, 3);
        Tensor tensor = tensorUnaryOperator.apply(sequence);
        assertEquals(Dimensions.of(tensor), Arrays.asList(3));
      }
    }
  }

  public void testOfFunction() {
    TensorUnaryOperator tensorUnaryOperator = BiinvariantMeanCenter.of(Se2BiinvariantMean.GLOBAL, BinomialWeights.INSTANCE);
    tensorUnaryOperator.apply(RandomVariate.of(UniformDistribution.unit(), 5, 3));
  }

  public void testFailNull() {
    try {
      BiinvariantMeanCenter.of(Se2BiinvariantMean.GLOBAL, (ScalarUnaryOperator) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
