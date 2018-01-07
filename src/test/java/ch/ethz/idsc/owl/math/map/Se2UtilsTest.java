// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2UtilsTest extends TestCase {
  public void testSimple1() {
    Tensor matrix = Se2Utils.toSE2Matrix(Tensors.vector(2, 3, 4));
    assertEquals(matrix.get(2), Tensors.vector(0, 0, 1));
    Scalar det = Det.of(matrix);
    assertTrue(Chop._14.close(det, RealScalar.ONE));
  }

  public void testFromMatrix() {
    Tensor x = Tensors.vector(2, 3, .5);
    Tensor matrix = Se2Utils.toSE2Matrix(x);
    Tensor y = Se2Utils.fromSE2Matrix(matrix);
    assertEquals(x, y);
  }

  public void testFromMatrix1() {
    Tensor x = Tensors.vector(2, 3, 3.5);
    Tensor matrix = Se2Utils.toSE2Matrix(x);
    Tensor y = Se2Utils.fromSE2Matrix(matrix);
    assertTrue(Chop._10.close(x.Get(2), y.Get(2).add(RealScalar.of(Math.PI * 2))));
  }

  public void testG0() {
    Tensor u = Tensors.vector(1.2, 0, 0);
    Tensor m = Se2Utils.integrate_g0(u);
    assertEquals(m, u);
  }

  public void testSome() {
    Tensor u = Tensors.vector(1.2, 0, 0.75);
    Tensor m = Se2Utils.toSE2Matrix(Se2Utils.integrate_g0(u)); // TODO make more efficient
    Tensor p = Tensors.vector(-2, 3);
    Tensor v = m.dot(p.copy().append(RealScalar.ONE));
    Tensor r = Se2Integrator.INSTANCE.spin(Se2Utils.integrate_g0(u), p.append(RealScalar.ZERO));
    assertEquals(r.extract(0, 2), v.extract(0, 2));
    Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u));
    assertEquals(se2ForwardAction.apply(p), v.extract(0, 2));
  }
}
