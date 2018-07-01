// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2GroupActionTest extends TestCase {
  public void testCirc() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Se2GroupAction se2GroupAction = new Se2GroupAction(xya);
      Tensor other = RandomVariate.of(distribution, 3);
      Tensor result = se2GroupAction.circ(other);
      Tensor prod = Se2Utils.toSE2Matrix(xya).dot(Se2Utils.toSE2Matrix(other));
      Tensor matrix = Se2Utils.toSE2Matrix(result);
      assertTrue(Chop._10.close(prod, matrix));
    }
  }

  public void testInverse() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Tensor result = new Se2GroupAction(xya).inverse();
      Tensor prod = Inverse.of(Se2Utils.toSE2Matrix(xya));
      Tensor matrix = Se2Utils.toSE2Matrix(result);
      assertTrue(Chop._10.close(prod, matrix));
    }
  }

  public void testInverseCirc() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Se2GroupAction se2GroupAction = new Se2GroupAction(xya);
      Tensor result = se2GroupAction.inverse();
      Tensor circ = se2GroupAction.circ(result);
      assertTrue(Chop._14.allZero(circ));
    }
  }

  public void testIntegrator() {
    Distribution distribution = NormalDistribution.of(0, 10);
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Se2GroupAction se2GroupAction = new Se2GroupAction(xya);
      Tensor v = RandomVariate.of(distribution, 3);
      Tensor other = Se2Utils.exp(v);
      Tensor result = se2GroupAction.circ(other);
      Tensor prod = Se2Integrator.INSTANCE.spin(xya, v);
      assertTrue(Chop._10.close(prod, result));
    }
  }
}
