// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
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

  public void testMod2Pi_1() {
    Tensor p = Tensors.vector(20, -43, -2 * Math.PI * 8);
    Tensor q = Tensors.vector(20, -43, +2 * Math.PI + 0.1);
    Tensor distance = Se2Wrap.INSTANCE.difference(p, q);
    Chop._10.requireClose(distance, Tensors.vector(0, 0, 0.1));
  }

  public void testMod2Pi_2() {
    Tensor p = Tensors.vector(0, 0, -2 * Math.PI * 3);
    Tensor q = Tensors.vector(0, 0, +2 * Math.PI + 0.1);
    Tensor difference = Se2Wrap.INSTANCE.difference(p, q);
    Chop._13.requireClose(difference, Tensors.vector(0, 0, 0.1));
  }

  public void testMod2PiUnits() {
    Tensor p1 = Tensors.fromString("{20[m], -43[m]}").append(RealScalar.of(-2 * Math.PI * 3));
    Tensor p2 = Tensors.fromString("{20[m], -43[m]}").append(RealScalar.of(-2 * Math.PI * 8));
    Tensor q = Tensors.fromString("{21[m], -48[m]}").append(RealScalar.of(+2 * Math.PI + 0.1));
    Tensor d1 = Se2Wrap.INSTANCE.difference(p1, q);
    Tensor d2 = Se2Wrap.INSTANCE.difference(p2, q);
    Chop._08.requireClose(d1, d2);
  }

  public void testEndPoints() {
    Distribution distribution = NormalDistribution.of(0, 10);
    for (int index = 0; index < 100; ++index) {
      Tensor p = RandomVariate.of(distribution, 3);
      p.set(So2.MOD, 2);
      Tensor q = RandomVariate.of(distribution, 3);
      q.set(So2.MOD, 2);
      Chop._14.requireClose(p, Se2Geodesic.INSTANCE.split(p, q, RealScalar.ZERO));
      Tensor r = Se2Geodesic.INSTANCE.split(p, q, RealScalar.ONE);
      if (!Chop._14.isClose(q, r))
        Chop._10.requireAllZero(Se2Wrap.INSTANCE.difference(q, r));
    }
  }

  public void testFail() {
    AssertFail.of(() -> 
      Se2Wrap.INSTANCE.represent(Tensors.vector(1, 2)));
  }

  public void testFailMatrix() {
    AssertFail.of(() -> 
      Se2Wrap.INSTANCE.represent(IdentityMatrix.of(3)));
  }
}
