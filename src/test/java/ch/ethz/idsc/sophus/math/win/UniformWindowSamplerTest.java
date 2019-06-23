// code by jph
package ch.ethz.idsc.sophus.math.win;

import java.util.function.Function;

import ch.ethz.idsc.sophus.math.SymmetricVectorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;
import ch.ethz.idsc.tensor.sca.win.TukeyWindow;
import junit.framework.TestCase;

public class UniformWindowSamplerTest extends TestCase {
  public void testNonContinuous() {
    Function<Integer, Tensor> function = UniformWindowSampler.of(GaussianWindow.FUNCTION);
    for (int count = 1; count < 6; ++count) {
      Tensor tensor = function.apply(count);
      assertEquals(tensor.length(), count);
      SymmetricVectorQ.require(tensor);
    }
  }

  public void testContinuous() {
    Function<Integer, Tensor> function = UniformWindowSampler.of(TukeyWindow.FUNCTION);
    for (int count = 1; count < 6; ++count) {
      Tensor tensor = function.apply(count);
      // System.out.println(tensor.map(Round._3));
      assertEquals(tensor.length(), count);
      SymmetricVectorQ.require(tensor);
    }
  }

  public void testGaussian() {
    Function<Integer, Tensor> function = UniformWindowSampler.of(GaussianWindow.FUNCTION);
    Tensor apply = function.apply(5);
    Chop._12.requireClose(apply, Tensors.vector( //
        0.08562916395501292, //
        0.24266759672960794, //
        0.34340647863075824, //
        0.24266759672960794, //
        0.08562916395501292));
  }
}
