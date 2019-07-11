// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
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
    Clips.interval(2.1, 2.2).requireInside(head);
  }

  public void testLog2Int() {
    int value = 1024;
    int bit = 31 - Integer.numberOfLeadingZeros(value);
    System.out.println(bit);
  }

  public void testSamplesSteps() {
    ClothoidTransition clothoidTransition = //
        new ClothoidTransition(Tensors.vector(1, 2, 3), Tensors.vector(4, 1, 5));
    try {
      clothoidTransition.sampled(0);
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertEquals(clothoidTransition.sampled(1).length(), 1);
    assertEquals(clothoidTransition.sampled(2).length(), 2);
    try {
      clothoidTransition.wrapped(0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
