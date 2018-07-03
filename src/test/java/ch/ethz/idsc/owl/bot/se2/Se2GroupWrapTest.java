// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2GroupWrapTest extends TestCase {
  public void testMod2Pi() {
    Tensor p = Tensors.vector(20, -30, -2 * Math.PI * 8);
    Tensor q = Tensors.vector(20, -30, +2 * Math.PI + 0.1);
    Se2GroupWrap se2Wrap = new Se2GroupWrap(Tensors.vector(1, 1, 1));
    Scalar distance = se2Wrap.distance(p, q);
    assertTrue(Chop._10.close(distance, RealScalar.of(0.1)));
  }
}
