// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Floor;
import junit.framework.TestCase;

public class Se2WrapTest extends TestCase {
  static Tensor convertToKey(Tensor eta, TensorUnaryOperator represent, Tensor x) {
    return eta.pmul(represent.apply(x)).map(Floor.FUNCTION);
  }

  public void testSe2xT_represent() {
    Tensor res = Se2Wrap.INSTANCE.represent(Tensors.vector(1, 1, 2 * Math.PI, 1));
    assertEquals(res, Tensors.vector(1, 1, 0, 1));
  }

  public void testMod2Pi() {
    Tensor p = Tensors.vector(20, -43, -2 * Math.PI * 8);
    Tensor q = Tensors.vector(20, -43, +2 * Math.PI + 0.1);
    Tensor distance = Se2Wrap.INSTANCE.difference(p, q);
    assertTrue(Chop._10.close(distance, Tensors.vector(0, 0, 0.1)));
  }

  public void testMod2PiUnits() {
    Tensor p1 = Tensors.fromString("{20[m], -43[m]}").append(RealScalar.of(-2 * Math.PI * 3));
    Tensor p2 = Tensors.fromString("{20[m], -43[m]}").append(RealScalar.of(-2 * Math.PI * 8));
    Tensor q = Tensors.fromString("{21[m], -48[m]}").append(RealScalar.of(+2 * Math.PI + 0.1));
    Tensor d1 = Se2Wrap.INSTANCE.difference(p1, q);
    Tensor d2 = Se2Wrap.INSTANCE.difference(p2, q);
    assertTrue(Chop._08.close(d1, d2));
  }

  public void testFail() {
    try {
      Se2Wrap.INSTANCE.represent(Tensors.vector(1, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailMatrix() {
    try {
      Se2Wrap.INSTANCE.represent(IdentityMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
