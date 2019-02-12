// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StaticHelperExtrapolationTest extends TestCase {
  public void testSimple() {
    Tensor mask = Tensors.vector(.5, .5);
    Tensor result = StaticHelperExtrapolation.splits(mask);
    assertEquals(Tensors.vector(.5, 3.0), result);
  }

  public void testElaborate() {
    WindowSideSampler windowSideSampler = new WindowSideSampler(SmoothingKernel.GAUSSIAN);
    Tensor mask = windowSideSampler.apply(6);
    Tensor result = StaticHelperExtrapolation.splits(mask);
    Tensor expect = Tensors.vector( //
        0.6045315182147757, 0.4610592079176246, 0.3765618899029577, 0.3135075836053491, //
        0.2603421497384919, 0.21295939875081005, 1.4534443632073355);
    assertEquals(expect, result);
  }

  public void testNoExtrapolation() {
    Tensor mask = Tensors.vector(1);
    Tensor result = StaticHelperExtrapolation.splits(mask);
    assertEquals(Tensors.vector(1), result);
  }

  public void testAffinityFail() {
    Tensor mask = Tensors.vector(.5, .8);
    try {
      StaticHelperExtrapolation.splits(mask);
      fail();
    } catch (Exception exception) {
      // ----
    }
  }
}
