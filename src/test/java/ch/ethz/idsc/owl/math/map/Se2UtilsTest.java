// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2UtilsTest extends TestCase {
  public void testSimple1() {
    Tensor matrix = Se2Utils.toSE2Matrix(Tensors.vector(2, 3, 4));
    assertEquals(matrix.get(2), Tensors.vector(0, 0, 1));
    Scalar det = Det.of(matrix);
    assertTrue(Chop._14.close(det, RealScalar.ONE));
  }

  public void test2PiModLogExp() {
    for (int n = -5; n <= 5; ++n) {
      double value = 1 + 2 * Math.PI * n;
      Tensor g = Tensors.vector(2, 3, value);
      Tensor x = Se2Utils.log(g);
      Tensor exp_x = Se2Utils.exp(x);
      assertEquals(exp_x.Get(2), RealScalar.of(value));
      assertTrue(Chop._13.close(g, exp_x));
    }
  }

  public void test2PiModExpLog() {
    for (int n = -5; n <= 5; ++n) {
      double value = 1 + 2 * Math.PI * n;
      Tensor x = Tensors.vector(2, 3, value);
      Tensor g = Se2Utils.exp(x);
      Tensor log_g = Se2Utils.log(g);
      assertTrue(Chop._13.close(x, log_g));
    }
  }

  public void testLog() {
    Distribution distribution = UniformDistribution.of(-25, 25);
    for (int index = 0; index < 10; ++index) {
      Tensor x = RandomVariate.of(distribution, 3);
      Tensor g = Se2Utils.exp(x);
      Tensor log_g = Se2Utils.log(g);
      assertTrue(Chop._13.close(x, log_g));
    }
  }

  public void testExp() {
    Distribution distribution = UniformDistribution.of(-25, 25);
    for (int index = 0; index < 10; ++index) {
      Tensor g = RandomVariate.of(distribution, 3);
      Tensor x = Se2Utils.log(g);
      Tensor exp_x = Se2Utils.exp(x);
      assertTrue(Chop._13.close(g, exp_x));
    }
  }

  public void testLog0() {
    Distribution distribution = UniformDistribution.of(-5, 5);
    for (int index = 0; index < 10; ++index) {
      Tensor x = RandomVariate.of(distribution, 2).append(RealScalar.ZERO);
      Tensor g0 = Se2Utils.exp(x);
      Tensor x2 = Se2Utils.log(g0);
      assertTrue(Chop._13.close(x, x2));
    }
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
    Tensor m = Se2Utils.exp(u);
    assertEquals(m, u);
  }

  public void testSome() {
    Tensor u = Tensors.vector(1.2, 0, 0.75);
    Tensor m = Se2Utils.toSE2Matrix(Se2Utils.exp(u));
    Tensor p = Tensors.vector(-2, 3);
    Tensor v = m.dot(p.copy().append(RealScalar.ONE));
    Tensor r = Se2Integrator.INSTANCE.spin(Se2Utils.exp(u), p.append(RealScalar.ZERO));
    assertEquals(r.extract(0, 2), v.extract(0, 2));
    Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.exp(u));
    assertEquals(se2ForwardAction.apply(p), v.extract(0, 2));
  }

  public void testTranslations() {
    Tensor xya = Tensors.vector(1, 2, 0);
    Tensor translate = Se2Utils.toSE2Translation(xya.extract(0, 2));
    assertEquals(Se2Utils.toSE2Matrix(xya), translate);
    assertTrue(ExactScalarQ.all(translate));
  }
}
