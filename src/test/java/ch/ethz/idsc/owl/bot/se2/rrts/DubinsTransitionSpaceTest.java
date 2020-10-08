// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparators;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DubinsTransitionSpaceTest extends TestCase {
  public void testLengthUnitless() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.fromString("{1, 2}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2, 6, 0}");
    Transition transition = Serialization.copy(DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH).connect(start, end));
    assertEquals(RealScalar.of(3).add(Pi.HALF), transition.length());
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  public void testLengthUnits() {
    Tensor start = Tensors.fromString("{1[m], 2[m]}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2[m], 6[m], 0}");
    Transition transition = DubinsTransitionSpace.of(Quantity.of(1, "m"), DubinsPathComparators.LENGTH).connect(start, end);
    assertEquals(Quantity.of(3 + Math.PI / 2, "m"), transition.length());
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  public void testSamples() {
    Tensor start = Tensors.fromString("{2, 1, 0}");
    Tensor end = Tensors.fromString("{6, 1, 0}");
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH);
    Transition transition = transitionSpace.connect(start, end);
    {
      Scalar res = RationalScalar.HALF;
      Tensor samples = transition.sampled(res);
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
    Tensor start = Tensors.fromString("{2, 1, 0}");
    Tensor end = Tensors.fromString("{6, 1, 0}");
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH);
    Transition transition = transitionSpace.connect(start, end);
    {
      Scalar res = RationalScalar.HALF;
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
    // wrap.spacing().stream().forEach(s -> assertEquals(res, s));
    // }
  }

  public void testRadiusFail() {
    AssertFail.of(() -> DubinsTransitionSpace.of(RealScalar.of(0.0), DubinsPathComparators.LENGTH));
    AssertFail.of(() -> DubinsTransitionSpace.of(RealScalar.of(-0.1), DubinsPathComparators.LENGTH));
  }

  public void testComparatorFail() {
    DubinsTransitionSpace.of(RealScalar.of(1.0), DubinsPathComparators.LENGTH);
    AssertFail.of(() -> DubinsTransitionSpace.of(RealScalar.of(1.0), null));
  }
}
