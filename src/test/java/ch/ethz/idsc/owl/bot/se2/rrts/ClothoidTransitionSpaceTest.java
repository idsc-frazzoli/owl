// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
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

public class ClothoidTransitionSpaceTest extends TestCase {
  public void testLength() throws ClassNotFoundException, IOException {
    Transition transition = Serialization.copy(ClothoidTransitionSpace.INSTANCE).connect( //
        RrtsNode.createRoot(Tensors.fromString("{1[m], 1[m], 0}"), RealScalar.ZERO), //
        Tensors.fromString("{2[m], 2[m]}").append(Pi.HALF));
    Chop._04.requireClose(transition.length(), Quantity.of(Pi.HALF, "m"));
  }

  public void testSamples() {
    RrtsNode start = RrtsNode.createRoot(Tensors.fromString("{1[m], 2[m], 1}"), RealScalar.ZERO);
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}");
    Transition transition = ClothoidTransitionSpace.INSTANCE.connect(start, end);
    {
      Scalar res = Quantity.of(.5, "m");
      Tensor samples = transition.sampled(res);
      assertEquals(16, samples.length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertNotSame(start.state(), samples.get(0));
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
    RrtsNode start = RrtsNode.createRoot(Tensors.fromString("{1[m], 2[m], 1}"), RealScalar.ZERO);
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}");
    Transition transition = ClothoidTransitionSpace.INSTANCE.connect(start, end);
    {
      Scalar res = Quantity.of(.5, "m");
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(16, wrap.samples().length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertNotSame(start.state(), wrap.samples().get(0));
      assertEquals(end, Last.of(wrap.samples()));
      assertTrue(wrap.spacing().extract(0, 16).stream().map(Tensor::Get) //
          .map(Sign::requirePositive) //
          .allMatch(s -> Scalars.lessEquals(s, res)));
    }
    // {
    // Scalar res = Quantity.of(.5, "m");
    // TransitionWrap wrap = transition.wrapped(res);
    // assertEquals(8, wrap.samples().length());
    // assertNotSame(start, wrap.samples().get(0));
    // assertEquals(end, Last.of(wrap.samples()));
    // wrap.spacing().extract(0, 8).stream().map(Tensor::Get) //
    // .map(Sign::requirePositive) //
    // .forEach(s -> Chop._01.requireClose(s, transition.length().divide(RealScalar.of(8))));
    // }
  }
}
