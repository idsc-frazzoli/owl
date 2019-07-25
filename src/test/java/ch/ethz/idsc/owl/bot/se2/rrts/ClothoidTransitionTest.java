// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidTransitionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    ClothoidTransition clothoidTransition = //
        Serialization.copy(new ClothoidTransition(Tensors.vector(1, 2, 3), Tensors.vector(4, 1, 5)));
    ClothoidTerminalRatios clothoidTerminalRatios = clothoidTransition.terminalRatios();
    Scalar head = clothoidTerminalRatios.head();
    Clips.interval(2.5, 2.6).requireInside(head);
  }

  public void testLog2Int() {
    int value = 1024;
    int bit = 31 - Integer.numberOfLeadingZeros(value);
    assertEquals(bit, 10);
  }

  public void testWrapped() {
    ClothoidTransition clothoidTransition = new ClothoidTransition(Tensors.vector(2, 3, 3), Tensors.vector(4, 1, 5));
    TransitionWrap transitionWrap = clothoidTransition.wrapped(RealScalar.of(.2));
    assertEquals(transitionWrap.samples().length(), transitionWrap.spacing().length());
  }

  public void testSamplesSteps() {
    ClothoidTransition clothoidTransition = //
        new ClothoidTransition(Tensors.vector(1, 2, 3), Tensors.vector(4, 1, 5));
    try {
      clothoidTransition.sampled(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertEquals(clothoidTransition.sampled(RealScalar.of(.2)).length(), 32);
    assertEquals(clothoidTransition.sampled(RealScalar.of(.1)).length(), 64);
    try {
      clothoidTransition.wrapped(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
