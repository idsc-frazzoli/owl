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
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = //
        Serialization.copy(RnTransitionSpace.INSTANCE.connect(start, end));
    Tensor tensor = rnTransition.sampled(RealScalar.of(0.1));
    assertEquals(tensor.length(), 90);
  }

  public void testLinearized() {
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(start, end);
    Tensor linearized = rnTransition.linearized(RealScalar.of(0.1));
    assertEquals(linearized, Tensors.fromString("{{1, 2}, {10, 2}}"));
  }

  public void testWrapped() {
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(start, end);
    TransitionWrap transitionWrap = rnTransition.wrapped(RealScalar.of(0.1));
    Tensor samples = transitionWrap.samples();
    Tensor spacing = transitionWrap.spacing();
    assertEquals(samples.length(), spacing.length());
    ExactTensorQ.of(spacing);
    Tensor diffnor = Tensor.of(Differences.of(samples).stream().map(Norm._2::ofVector));
    assertEquals(spacing.extract(0, diffnor.length()), diffnor);
  }

  public void testFail() {
    Tensor start = Tensors.vector(1, 2);
    Tensor end = Tensors.vector(10, 2);
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(start, end);
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
