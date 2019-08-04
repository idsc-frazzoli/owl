// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.math.HeadTailInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class ClothoidTransitionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    ClothoidTransition clothoidTransition = //
        Serialization.copy(ClothoidTransition.of(Tensors.vector(1, 2, 3), Tensors.vector(4, 1, 5)));
    HeadTailInterface clothoidTerminalRatio = clothoidTransition.terminalRatios();
    Scalar head = clothoidTerminalRatio.head();
    Clips.interval(2.5, 2.6).requireInside(head);
  }

  public void testLog2Int() {
    int value = 1024;
    int bit = 31 - Integer.numberOfLeadingZeros(value);
    assertEquals(bit, 10);
  }

  public void testWrapped() {
    ClothoidTransition clothoidTransition = ClothoidTransition.of(Tensors.vector(2, 3, 3), Tensors.vector(4, 1, 5));
    TransitionWrap transitionWrap = clothoidTransition.wrapped(RealScalar.of(.2));
    assertEquals(transitionWrap.samples().length(), transitionWrap.spacing().length());
    assertTrue(transitionWrap.spacing().stream().map(Tensor::Get).allMatch(Sign::isPositive));
  }

  public void testSamples2() {
    ClothoidTransition clothoidTransition = //
        ClothoidTransition.of(Tensors.vector(0, 0, 0), Tensors.vector(4, 0, 0));
    Chop._12.requireClose(clothoidTransition.length(), RealScalar.of(4));
    assertEquals(clothoidTransition.sampled(RealScalar.of(2)).length(), 2);
    assertEquals(clothoidTransition.sampled(RealScalar.of(1.9)).length(), 4);
  }

  public void testSamplesSteps() {
    ClothoidTransition clothoidTransition = //
        ClothoidTransition.of(Tensors.vector(1, 2, 3), Tensors.vector(4, 1, 5));
    assertEquals(clothoidTransition.sampled(RealScalar.of(.2)).length(), 32);
    assertEquals(clothoidTransition.sampled(RealScalar.of(.1)).length(), 64);
    assertEquals(clothoidTransition.linearized(RealScalar.of(.2)).length(), 33);
    assertEquals(clothoidTransition.linearized(RealScalar.of(.1)).length(), 65);
  }

  public void testFails() {
    ClothoidTransition clothoidTransition = //
        ClothoidTransition.of(Tensors.vector(1, 2, 3), Tensors.vector(4, 1, 5));
    try {
      clothoidTransition.sampled(RealScalar.of(-0.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      clothoidTransition.sampled(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      clothoidTransition.wrapped(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      clothoidTransition.linearized(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
