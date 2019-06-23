// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSamplesWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ClothoidTransitionSpaceTest extends TestCase {
  public void testLength() {
    Transition transition = ClothoidTransitionSpace.INSTANCE.connect( //
        Tensors.fromString("{1[m],1[m],0}"), //
        Tensors.fromString("{2[m],2[m]}").append(Pi.HALF));
    Chop._15.requireClose(transition.length(), Quantity.of(Pi.HALF, "m"));
  }

  public void testSamples() {
    Tensor start = Tensors.fromString("{1[m],2[m],1}");
    Tensor end = Tensors.fromString("{1[m],6[m],3}");
    Transition transition = ClothoidTransitionSpace.INSTANCE.connect(start, end);
    {
      Scalar res = Quantity.of(.5, "m");
      TransitionSamplesWrap wrap = transition.sampled(res);
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
    {
      TransitionSamplesWrap wrap = transition.sampled(8);
      assertEquals(8, wrap.samples().length());
      assertEquals(start, wrap.samples().get(0));
      assertNotSame(end, Last.of(wrap.samples()));
      assertEquals(Quantity.of(0, "m"), wrap.spacing().Get(0));
      wrap.spacing().extract(1, 8).stream().map(Tensor::Get) //
          .map(Sign::requirePositive) //
          .forEach(s -> Chop._01.requireClose(s, transition.length().divide(RealScalar.of(8))));
    }
  }
}
