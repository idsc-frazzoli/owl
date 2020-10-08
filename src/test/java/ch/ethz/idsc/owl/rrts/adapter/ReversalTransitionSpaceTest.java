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
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ReversalTransitionSpaceTest extends TestCase {
  public void testLength() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.fromString("{1[m], 1[m]}").append(Pi.VALUE);
    Tensor end = Tensors.fromString("{2[m], 2[m]}").append(Pi.HALF.negate());
    Transition transition = Serialization.copy(ReversalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC)).connect(start, end);
    Chop._03.requireClose(transition.length(), Quantity.of(Pi.HALF, "m"));
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  public void testSamples() {
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}").add(Tensors.vector(0, 0, Math.PI));
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}").add(Tensors.vector(0, 0, Math.PI));
    Transition transition = ReversalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC).connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      Tensor samples = transition.sampled(res);
      assertEquals(10, samples.length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
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
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}").add(Tensors.vector(0, 0, Math.PI));
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}").add(Tensors.vector(0, 0, Math.PI));
    Transition transition = ReversalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC).connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(10, wrap.samples().length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertNotSame(start, wrap.samples().get(0));
      assertEquals(end, Last.of(wrap.samples()));
      assertTrue(wrap.spacing().extract(0, 10).stream().map(Tensor::Get) //
          .map(Sign::requirePositive) //
          .allMatch(s -> Scalars.lessEquals(s, res)));
    }
    // {
    // TransitionWrap wrap = transition.wrapped(8);
    // assertEquals(8, wrap.samples().length());
    // assertNotSame(start, wrap.samples().get(0));
    // assertEquals(end, Last.of(wrap.samples()));
    // wrap.spacing().extract(0, 8).stream().map(Tensor::Get) //
    // .map(Sign::requirePositive) //
    // .forEach(s -> Chop._01.requireClose(s, transition.length().divide(RealScalar.of(8))));
    // }
  }
}
