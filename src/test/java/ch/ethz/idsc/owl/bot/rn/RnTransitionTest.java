// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class RnTransitionTest extends TestCase {
  public void testSampled() throws ClassNotFoundException, IOException {
    RnTransition rnTransition = //
        Serialization.copy(RnTransitionSpace.INSTANCE.connect(Tensors.vector(1, 2), Tensors.vector(10, 2)));
    Tensor tensor = rnTransition.sampled(RealScalar.of(0.1));
    assertEquals(tensor.length(), 90);
  }

  public void testWrapped() {
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(Tensors.vector(1, 2), Tensors.vector(10, 2));
    TransitionWrap transitionWrap = rnTransition.wrapped(RealScalar.of(0.1));
    Tensor samples = transitionWrap.samples();
    Tensor spacing = transitionWrap.spacing();
    assertEquals(samples.length(), spacing.length());
    ExactTensorQ.of(spacing);
    Tensor diffnor = Tensor.of(Differences.of(samples).stream().map(Norm._2::ofVector));
    assertEquals(spacing.extract(0, diffnor.length()), diffnor);
  }

  public void testFail() {
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(Tensors.vector(1, 2), Tensors.vector(10, 2));
    rnTransition.sampled(RealScalar.of(100));
    try {
      rnTransition.sampled(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      rnTransition.sampled(RealScalar.of(-0.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
