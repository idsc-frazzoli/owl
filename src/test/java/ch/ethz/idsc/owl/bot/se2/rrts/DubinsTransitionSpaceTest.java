// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSamplesWrap;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DubinsTransitionSpaceTest extends TestCase {
  public void testLengthUnitless() {
    Tensor start = Tensors.fromString("{1,2}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2,6,0}");
    Transition transition = DubinsTransitionSpace.withRadius(RealScalar.ONE).connect(start, end);
    assertEquals(RealScalar.of(3).add(Pi.HALF), transition.length());
  }

  /* FIXME units do not work
  public void testLengthUnits() {
    Tensor start = Tensors.fromString("{1[m],2[m]}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2[m],6[m],0}");
    Transition transition = DubinsTransitionSpace.withRadius(Quantity.of(1, "m")).connect(start, end);
    assertEquals(Quantity.of(3 + Math.PI / 2, "m"), transition.length());
  }
  */

  public void testSamples() {
    Tensor start = Tensors.fromString("{2,1,0}");
    Tensor end = Tensors.fromString("{6,1,0}");
    Transition transition = DubinsTransitionSpace.withRadius(RealScalar.ONE).connect(start, end);
    {
      Scalar res = RationalScalar.HALF;
      TransitionSamplesWrap wrap = transition.sampled(res);
      assertEquals(8, wrap.samples().length());
      assertEquals(start, wrap.samples().get(0));
      assertNotSame(end, Last.of(wrap.samples()));
      assertEquals(RealScalar.ZERO, wrap.spacing().Get(0));
      assertEquals(res, wrap.spacing().Get(1));
    }
    {
      TransitionSamplesWrap wrap = transition.sampled(8);
      assertEquals(8, wrap.samples().length());
      assertEquals(start, wrap.samples().get(0));
      assertNotSame(end, Last.of(wrap.samples()));
      assertEquals(RealScalar.ZERO, wrap.spacing().Get(0));
      assertEquals(transition.length().divide(RealScalar.of(8)), wrap.spacing().Get(1));
    }
  }
}
