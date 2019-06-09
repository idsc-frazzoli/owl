// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.Arrays;

import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class BiinvariantMeanCenterTest extends TestCase {
  public void testSe2() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          BiinvariantMeanCenter.of(Se2BiinvariantMean.DEFAULT, smoothingKernel);
      Distribution distribution = UniformDistribution.unit();
      Tensor sequence = RandomVariate.of(distribution, 7, 3);
      Tensor tensor = tensorUnaryOperator.apply(sequence);
      assertEquals(Dimensions.of(tensor), Arrays.asList(3));
    }
  }

  public void testFailNull() {
    try {
      BiinvariantMeanCenter.of(Se2BiinvariantMean.DEFAULT, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
