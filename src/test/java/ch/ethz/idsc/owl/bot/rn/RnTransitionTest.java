// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnTransitionTest extends TestCase {
  public void testFail() {
    RnTransition rnTransition = RnTransitionSpace.INSTANCE.connect(Tensors.vector(1, 2), Tensors.vector(10, 2));
    Tensor tensor = rnTransition.sampled(RealScalar.of(100));
    System.out.println(tensor.length());
    try {
      rnTransition.sampled(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      rnTransition.sampled(RealScalar.of(-0.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
