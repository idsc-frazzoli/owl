// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringGroupElementTest extends TestCase {
  public void testCirc() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Se2CoveringGroupElement se2GroupAction = new Se2CoveringGroupElement(xya);
      Tensor other = RandomVariate.of(distribution, 3);
      Tensor result = se2GroupAction.combine(other);
      Tensor prod = Se2Utils.toSE2Matrix(xya).dot(Se2Utils.toSE2Matrix(other));
      Tensor matrix = Se2Utils.toSE2Matrix(result);
      assertTrue(Chop._10.close(prod, matrix));
    }
  }

  public void testInverse() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Tensor result = new Se2CoveringGroupElement(xya).inverse().combine(Array.zeros(3));
      Tensor prod = Inverse.of(Se2Utils.toSE2Matrix(xya));
      Tensor matrix = Se2Utils.toSE2Matrix(result);
      assertTrue(Chop._10.close(prod, matrix));
    }
  }

  public void testInverseCirc() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Se2CoveringGroupElement se2GroupAction = new Se2CoveringGroupElement(xya);
      Tensor result = se2GroupAction.inverse().combine(Array.zeros(3));
      Tensor circ = se2GroupAction.combine(result);
      assertTrue(Chop._14.allZero(circ));
    }
  }

  public void testIntegrator() {
    Distribution distribution = NormalDistribution.of(0, 10);
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(distribution, 3);
      Se2CoveringGroupElement se2GroupAction = new Se2CoveringGroupElement(xya);
      Tensor v = RandomVariate.of(distribution, 3);
      Tensor other = Se2CoveringExponential.INSTANCE.exp(v);
      Tensor result = se2GroupAction.combine(other);
      Tensor prod = Se2CoveringIntegrator.INSTANCE.spin(xya, v);
      assertTrue(Chop._10.close(prod, result));
    }
  }

  public void testQuantity() {
    Tensor xya = Tensors.fromString("{1[m],2[m],.34}");
    Tensor oth = Tensors.fromString("{-.3[m],.8[m],-.5}");
    Se2CoveringGroupElement se2GroupAction = new Se2CoveringGroupElement(xya);
    Tensor inverse = se2GroupAction.inverse().combine(Array.zeros(3));
    assertEquals(inverse, Tensors.fromString("{-1.6097288498099749[m], -1.552022238915878[m], -0.34}"));
    Tensor circ = se2GroupAction.combine(oth);
    assertEquals(circ, Tensors.fromString("{0.4503839266288446[m], 2.654157604780433[m], -0.15999999999999998}"));
  }

  public void testMatrixAction() {
    Distribution distribution = NormalDistribution.of(0, 10);
    for (int index = 0; index < 10; ++index) {
      Tensor xya1 = RandomVariate.of(distribution, 3);
      Tensor xya2 = RandomVariate.of(distribution, 3);
      Tensor xya3 = new Se2CoveringGroupElement(xya1).combine(xya2);
      Tensor xyam = Se2Utils.toSE2Matrix(xya1).dot(Se2Utils.toSE2Matrix(xya2));
      assertTrue(Chop._12.close(Se2Utils.toSE2Matrix(xya3), xyam));
    }
  }
}
