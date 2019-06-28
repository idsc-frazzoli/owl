// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSamplesWrap;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DubinsTransitionSpaceTest extends TestCase {
  public void testLengthUnitless() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.fromString("{1,2}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2,6,0}");
    Transition transition = Serialization.copy(DubinsTransitionSpace.of(RealScalar.ONE).connect(start, end));
    assertEquals(RealScalar.of(3).add(Pi.HALF), transition.length());
  }

  public void testLengthUnits() {
    Tensor start = Tensors.fromString("{1[m],2[m]}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2[m],6[m],0}");
    Transition transition = DubinsTransitionSpace.of(Quantity.of(1, "m")).connect(start, end);
    assertEquals(Quantity.of(3 + Math.PI / 2, "m"), transition.length());
  }

  public void testSamples() {
    Tensor start = Tensors.fromString("{2,1,0}");
    Tensor end = Tensors.fromString("{6,1,0}");
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.ONE);
    Transition transition = transitionSpace.connect(start, end);
    {
      Scalar res = RationalScalar.HALF;
      Tensor samples = transition.sampled(res);
      assertEquals(8, samples.length());
      assertEquals(start, samples.get(0));
      assertNotSame(end, Last.of(samples));
      TransitionSamplesWrap wrap = TransitionSamplesWrap.of(samples, transitionSpace);
      assertEquals(RealScalar.ZERO, wrap.spacing().Get(0));
      assertEquals(res, wrap.spacing().Get(1));
    }
    {
      Tensor samples = transition.sampled(8);
      assertEquals(8, samples.length());
      assertEquals(start, samples.get(0));
      assertNotSame(end, Last.of(samples));
      TransitionSamplesWrap wrap = TransitionSamplesWrap.of(samples, transitionSpace);
      assertEquals(RealScalar.ZERO, wrap.spacing().Get(0));
      assertEquals(transition.length().divide(RealScalar.of(8)), wrap.spacing().Get(1));
    }
  }
}
