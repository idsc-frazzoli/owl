// code by jph, gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ReversalTransitionSpaceTest extends TestCase {
  public void testLength() throws ClassNotFoundException, IOException {
    Transition transition = Serialization.copy(ReversalTransitionSpace.of(ClothoidTransitionSpace.INSTANCE)).connect( //
        Tensors.fromString("{1[m], 1[m]}").append(Pi.VALUE), //
        Tensors.fromString("{2[m], 2[m]}").append(Pi.HALF.negate()));
    Chop._15.requireClose(transition.length(), Quantity.of(Pi.HALF, "m"));
  }

  public void testSamples() {
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}").add(Tensors.vector(0, 0, Math.PI));
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}").add(Tensors.vector(0, 0, Math.PI));
    Transition transition = ReversalTransitionSpace.of(ClothoidTransitionSpace.INSTANCE).connect(start, end);
    {
      Scalar res = Quantity.of(.5, "m");
      Tensor samples = transition.sampled(res);
      assertEquals(16, samples.length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertEquals(start, samples.get(0));
      assertNotSame(end, Last.of(samples));
    }
    // {
    // Tensor samples = transition.sampled(8);
    // assertEquals(8, samples.length());
    // assertEquals(start, samples.get(0));
    // assertNotSame(end, Last.of(samples));
    // }
  }

  public void testWrap() {
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}").add(Tensors.vector(0, 0, Math.PI));
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}").add(Tensors.vector(0, 0, Math.PI));
    Transition transition = ReversalTransitionSpace.of(ClothoidTransitionSpace.INSTANCE).connect(start, end);
    {
      Scalar res = Quantity.of(.5, "m");
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(16, wrap.samples().length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertEquals(start, wrap.samples().get(0));
      assertNotSame(end, Last.of(wrap.samples()));
      assertEquals(Quantity.of(0, "m"), wrap.spacing().Get(0));
      assertTrue(wrap.spacing().extract(1, 16).stream().map(Tensor::Get) //
          .map(Sign::requirePositive) //
          .allMatch(s -> Scalars.lessEquals(s, res)));
    }
    // {
    // TransitionWrap wrap = transition.wrapped(8);
    // // TODO GJOEL/JPH jan broke test
    // // assertEquals(8, wrap.samples().length());
    // // assertEquals(start, wrap.samples().get(0));
    // assertNotSame(end, Last.of(wrap.samples()));
    // assertEquals(Quantity.of(0, "m"), wrap.spacing().Get(0));
    // // wrap.spacing().extract(1, 8).stream().map(Tensor::Get) //
    // // .map(Sign::requirePositive) //
    // // .forEach(s -> Chop._01.requireClose(s, transition.length().divide(RealScalar.of(8))));
    // }
  }
}
