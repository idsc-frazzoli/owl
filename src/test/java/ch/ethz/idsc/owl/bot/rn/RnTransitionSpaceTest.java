// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RnTransitionSpaceTest extends TestCase {
  public void testLength() throws ClassNotFoundException, IOException {
    Transition transition = Serialization.copy(RnTransitionSpace.INSTANCE).connect( //
        Tensors.fromString("{1[m], 2[m]}"), //
        Tensors.fromString("{1[m], 6[m]}"));
    assertEquals(transition.length(), Quantity.of(4, "m"));
    ExactScalarQ.require(transition.length());
  }

  public void testSamples() {
    Tensor start = Tensors.fromString("{1[m], 2[m]}");
    Tensor end = Tensors.fromString("{1[m], 6[m]}");
    Transition transition = RnTransitionSpace.INSTANCE.connect(start, end);
    {
      Scalar res = Quantity.of(.5, "m");
      Tensor samples = transition.sampled(res);
      ExactTensorQ.require(samples);
      assertEquals(8, samples.length());
      assertNotSame(start, samples.get(0));
      assertEquals(end, Last.of(samples));
    }
    // {
    // Tensor samples = transition.sampled(8);
    // assertEquals(8, samples.length());
    // assertNotSame(start, samples.get(0));
    // assertEquals(end, Last.of(samples));
    // }
  }

  public void testWrap() {
    Tensor start = Tensors.fromString("{1[m], 2[m]}");
    Tensor end = Tensors.fromString("{1[m], 6[m]}");
    Transition transition = RnTransitionSpace.INSTANCE.connect(start, end);
    {
      Scalar res = Quantity.of(.5, "m");
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(8, wrap.samples().length());
      assertNotSame(start, wrap.samples().get(0));
      assertEquals(end, Last.of(wrap.samples()));
      wrap.spacing().stream().forEach(s -> assertEquals(res, s));
    }
    // {
    // TransitionWrap wrap = transition.wrapped(8);
    // assertEquals(8, wrap.samples().length());
    // assertNotSame(start, wrap.samples().get(0));
    // assertEquals(end, Last.of(wrap.samples()));
    // wrap.spacing().stream().forEach(s -> assertEquals(transition.length().divide(RealScalar.of(8)), s));
    // }
  }
}
