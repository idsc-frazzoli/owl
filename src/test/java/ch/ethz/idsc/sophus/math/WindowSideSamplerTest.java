// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.VectorTotal;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class WindowSideSamplerTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorTotal.FUNCTION);

  public void testExact() {
    WindowSideSampler windowSideSampler = new WindowSideSampler(SmoothingKernel.HANN);
    WindowCenterSampler windowCenterSampler = new WindowCenterSampler(SmoothingKernel.HANN);
    for (int extent = 0; extent < 3; ++extent) {
      Tensor tensor = windowSideSampler.apply(extent);
      Tensor expect = NORMALIZE.apply(windowCenterSampler.apply(extent).extract(0, extent + 1));
      assertEquals(tensor, expect);
      ExactTensorQ.require(tensor);
      ExactTensorQ.require(expect);
    }
  }

  public void testNumeric() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      WindowSideSampler windowSideSampler = new WindowSideSampler(smoothingKernel);
      WindowCenterSampler windowCenterSampler = new WindowCenterSampler(smoothingKernel);
      for (int extent = 0; extent < 7; ++extent) {
        Tensor tensor = windowSideSampler.apply(extent);
        Tensor expect = NORMALIZE.apply(windowCenterSampler.apply(extent).extract(0, extent + 1));
        Chop._12.requireClose(tensor, expect);
      }
    }
  }
}
