// code by jph
package ch.ethz.idsc.sophus.math.win;

import java.util.function.Function;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.NormalizeTotal;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class WindowSidedSamplerTest extends TestCase {
  public void testSpecific() {
    Function<Integer, Tensor> windowSideSampler = WindowSidedSampler.of(SmoothingKernel.BARTLETT);
    assertEquals(windowSideSampler.apply(0), Tensors.fromString("{1}"));
    assertEquals(windowSideSampler.apply(1), Tensors.fromString("{1/3, 2/3}"));
    assertEquals(windowSideSampler.apply(2), Tensors.fromString("{1/6, 1/3, 1/2}"));
  }

  public void testExact() {
    Function<Integer, Tensor> windowSideSampler = WindowSidedSampler.of(SmoothingKernel.HANN);
    Function<Integer, Tensor> windowCenterSampler = WindowCenterSampler.of(SmoothingKernel.HANN);
    for (int extent = 0; extent < 3; ++extent) {
      Tensor tensor = windowSideSampler.apply(extent);
      Tensor expect = NormalizeTotal.FUNCTION.apply(windowCenterSampler.apply(extent).extract(0, extent + 1));
      assertEquals(tensor, expect);
      ExactTensorQ.require(tensor);
      ExactTensorQ.require(expect);
    }
  }

  public void testNumeric() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      Function<Integer, Tensor> windowSideSampler = WindowSidedSampler.of(smoothingKernel);
      Function<Integer, Tensor> windowCenterSampler = WindowCenterSampler.of(smoothingKernel);
      for (int extent = 0; extent < 7; ++extent) {
        Tensor tensor = windowSideSampler.apply(extent);
        Tensor expect = NormalizeTotal.FUNCTION.apply(windowCenterSampler.apply(extent).extract(0, extent + 1));
        Chop._12.requireClose(tensor, expect);
      }
    }
  }

  public void testMemo() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      Function<Integer, Tensor> function = WindowSidedSampler.of(smoothingKernel);
      for (int count = 0; count < 5; ++count) {
        Tensor val1 = function.apply(count);
        Tensor val2 = function.apply(count);
        assertTrue(val1 == val2); // equal by reference
        try {
          val1.set(RealScalar.ZERO, 0);
          fail();
        } catch (Exception exception) {
          // ---
        }
      }
    }
  }
}
