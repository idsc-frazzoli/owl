// code by jph
package ch.ethz.idsc.sophus.math.win;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class WindowCenterSamplerTest extends TestCase {
  public void testMemo() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      Function<Integer, Tensor> function = WindowCenterSampler.of(smoothingKernel);
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

  public void testFailNull() {
    try {
      WindowCenterSampler.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
