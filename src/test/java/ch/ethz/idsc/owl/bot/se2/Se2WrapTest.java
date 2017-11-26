// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Floor;
import junit.framework.TestCase;

public class Se2WrapTest extends TestCase {
  static Tensor convertToKey(Tensor eta, TensorUnaryOperator represent, Tensor x) {
    return eta.pmul(represent.apply(x)).map(Floor.FUNCTION);
  }

  public void testSimple() {
    Se2Wrap se2Wrap = new Se2Wrap(Tensors.vector(1, 1, 2));
    Tensor eta = Tensors.vector(3, 3, 50 / Math.PI);
    {
      Tensor rep = convertToKey(eta, se2Wrap::represent, Tensors.vector(0, 0, 0));
      assertEquals(rep.Get(2), RealScalar.ZERO);
    }
    {
      Tensor rep = convertToKey(eta, se2Wrap::represent, Tensors.vector(0, 0, Math.PI - 0.0001));
      assertEquals(rep.Get(2), RealScalar.of(49));
    }
    {
      Tensor rep = convertToKey(eta, se2Wrap::represent, Tensors.vector(0, 0, Math.PI));
      assertEquals(rep.Get(2), RealScalar.of(50));
    }
    {
      Tensor rep = convertToKey(eta, se2Wrap::represent, Tensors.vector(0, 0, 2 * Math.PI - 0.0001));
      assertEquals(rep.Get(2), RealScalar.of(99));
    }
    {
      Tensor rep = convertToKey(eta, se2Wrap::represent, Tensors.vector(0, 0, -0.0001));
      assertEquals(rep.Get(2), RealScalar.of(99));
    }
  }

  public void testSe2xT_represent() {
    Se2Wrap se2Wrap = new Se2Wrap(Tensors.vector(1, 1, 2, 1));
    Tensor res = se2Wrap.represent(Tensors.vector(1, 1, 2 * Math.PI, 1));
    assertEquals(res, Tensors.vector(1, 1, 0, 1));
  }

  public void testSe2xT_distance() {
    Se2Wrap se2Wrap = new Se2Wrap(Tensors.fromString("{2[CHF],3[CHF],4[CHF],10[CHF]}"));
    Scalar res = se2Wrap.distance(Tensors.vector(2, 3, 2 * Math.PI, 1), Tensors.vector(1, 1, 0 * Math.PI, 1));
    assertTrue(Chop._12.close(res, Quantity.of(Math.sqrt(2 * 2 + 2 * 3 * 6), "CHF")));
  }

  public void testFail() {
    try {
      new Se2Wrap(Tensors.vector(1, 2));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
