// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2MatrixTest extends TestCase {
  public void testSimple1() {
    Tensor matrix = Se2Matrix.of(Tensors.vector(2, 3, 4));
    assertEquals(matrix.get(2), Tensors.vector(0, 0, 1));
    Scalar det = Det.of(matrix);
    Chop._14.requireClose(det, RealScalar.ONE);
  }

  public void test2PiModLogExp() {
    for (int n = -5; n <= 5; ++n) {
      double value = 1 + 2 * Math.PI * n;
      Tensor g = Tensors.vector(2, 3, value);
      Tensor x = Se2CoveringExponential.INSTANCE.log(g);
      Tensor exp_x = Se2CoveringExponential.INSTANCE.exp(x);
      assertEquals(exp_x.Get(2), RealScalar.of(value));
      Chop._13.requireClose(g, exp_x);
    }
  }

  public void test2PiModExpLog() {
    for (int n = -5; n <= 5; ++n) {
      double value = 1 + 2 * Math.PI * n;
      Tensor x = Tensors.vector(2, 3, value);
      Tensor g = Se2CoveringExponential.INSTANCE.exp(x);
      Tensor log_g = Se2CoveringExponential.INSTANCE.log(g);
      Chop._13.requireClose(x, log_g);
    }
  }

  public void testLog() {
    Distribution distribution = UniformDistribution.of(-25, 25);
    for (int index = 0; index < 10; ++index) {
      Tensor x = RandomVariate.of(distribution, 3);
      Tensor g = Se2CoveringExponential.INSTANCE.exp(x);
      Tensor log_g = Se2CoveringExponential.INSTANCE.log(g);
      Chop._10.requireClose(x, log_g);
    }
  }

  public void testExp() {
    Distribution distribution = UniformDistribution.of(-25, 25);
    for (int index = 0; index < 10; ++index) {
      Tensor g = RandomVariate.of(distribution, 3);
      Tensor x = Se2CoveringExponential.INSTANCE.log(g);
      Tensor exp_x = Se2CoveringExponential.INSTANCE.exp(x);
      Chop._10.requireClose(g, exp_x);
    }
  }

  public void testLog0() {
    Distribution distribution = UniformDistribution.of(-5, 5);
    for (int index = 0; index < 10; ++index) {
      Tensor x = RandomVariate.of(distribution, 2).append(RealScalar.ZERO);
      Tensor g0 = Se2CoveringExponential.INSTANCE.exp(x);
      Tensor x2 = Se2CoveringExponential.INSTANCE.log(g0);
      Chop._13.requireClose(x, x2);
    }
  }

  public void testFromMatrix() {
    Tensor x = Tensors.vector(2, 3, .5);
    Tensor matrix = Se2Matrix.of(x);
    Tensor y = Se2Matrix.toVector(matrix);
    assertEquals(x, y);
  }

  public void testFromMatrix1() {
    Tensor x = Tensors.vector(2, 3, 3.5);
    Tensor matrix = Se2Matrix.of(x);
    Tensor y = Se2Matrix.toVector(matrix);
    Chop._10.requireClose(x.Get(2), y.Get(2).add(Pi.TWO));
  }

  public void testG0() {
    Tensor u = Tensors.vector(1.2, 0, 0);
    Tensor m = Se2CoveringExponential.INSTANCE.exp(u);
    assertEquals(m, u);
  }

  public void testTranslations() {
    Tensor xya = Tensors.vector(1, 2, 0);
    Tensor translate = Se2Matrix.translation(xya.extract(0, 2));
    assertEquals(Se2Matrix.of(xya), translate);
    ExactTensorQ.require(translate);
  }

  public void testFlipY() {
    Tensor tensor = Se2Matrix.flipY(5);
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.fromString("{{1, 0, 0}, {0, -1, 5}, {0, 0, 1}}"));
    assertEquals(Det.of(tensor), RealScalar.ONE.negate());
  }
}
