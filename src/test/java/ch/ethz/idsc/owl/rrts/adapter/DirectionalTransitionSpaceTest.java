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
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class DirectionalTransitionSpaceTest extends TestCase {
  public void testLength() throws ClassNotFoundException, IOException {
    Tensor a = Tensors.fromString("{1[m], 1[m], 0}");
    Tensor b = Tensors.fromString("{2[m], 2[m]}").append(Pi.HALF);
    _checkLength(a, b);
    _checkLength(b, a);
  }

  private static void _checkLength(Tensor start, Tensor end) throws ClassNotFoundException, IOException {
    Transition transition = Serialization.copy(DirectionalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC)) //
        .connect(start, end);
    Chop._03.requireClose(transition.length(), Quantity.of(Pi.HALF, "m"));
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  public void testSamples() {
    Tensor a = Tensors.fromString("{1[m], 2[m], 1}");
    Tensor b = Tensors.fromString("{1[m], 6[m], 3}");
    _checkSamples(a, b);
    _checkSamples(b, a);
  }

  private static void _checkSamples(Tensor start, Tensor end) {
    Transition transition = DirectionalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC) //
        .connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      Tensor samples = transition.sampled(res);
      assertEquals(10, samples.length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertNotSame(start, samples.get(0));
      Tolerance.CHOP.requireClose(end, Last.of(samples));
    }
    // {
    // Tensor samples = transition.sampled(8);
    // assertEquals(8, samples.length());
    // assertNotSame(start, samples.get(0));
    // assertEquals(end, Last.of(samples));
    // }
  }

  public void testWrap() {
    Tensor a = Tensors.fromString("{1[m], 2[m], 1}");
    Tensor b = Tensors.fromString("{1[m], 6[m], 3}");
    testWrap(a, b);
    testWrap(b, a);
  }

  public void testWrap(Tensor start, Tensor end) {
    Transition transition = DirectionalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC) //
        .connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(10, wrap.samples().length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertNotSame(start, wrap.samples().get(0));
      Tolerance.CHOP.requireClose(end, Last.of(wrap.samples()));
      assertTrue(wrap.spacing().extract(0, 10).stream().map(Scalar.class::cast) //
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
