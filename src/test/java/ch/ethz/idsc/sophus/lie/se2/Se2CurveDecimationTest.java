// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import java.io.IOException;

import ch.ethz.idsc.sophus.app.io.GokartPoseData;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.crv.CurveDecimation;
import ch.ethz.idsc.sophus.crv.CurveDecimation.Result;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.subare.util.RandomChoice;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CurveDecimationTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(Se2CurveDecimation.of(RealScalar.of(0.3)));
    Tensor p = Tensors.vector(4, 3, 7);
    Tensor q = Tensors.vector(1, 2, 5);
    ScalarTensorFunction scalarTensorFunction = Se2Geodesic.INSTANCE.curve(p, q);
    Tensor m1 = scalarTensorFunction.apply(RealScalar.of(.3));
    Tensor m2 = scalarTensorFunction.apply(RealScalar.of(.8));
    Tensor curve = Tensors.of(p, m1, m2, q);
    Tensor tensor = tensorUnaryOperator.apply(curve);
    assertEquals(tensor.length(), 2);
    assertEquals(tensor, Tensors.of(p, q));
  }

  public void testGokart() throws ClassNotFoundException, IOException {
    GokartPoseData gokartPoseData = GokartPoseDataV2.RACING_DAY;
    CurveDecimation curveDecimation = Serialization.copy(Se2CurveDecimation.of(RealScalar.of(0.3)));
    for (String name : gokartPoseData.list()) {
      Tensor matrix = gokartPoseData.getPose(name, 2000);
      Tensor copy = matrix.copy();
      Result curveDecimationResult = curveDecimation.evaluate(matrix.unmodifiable());
      Tensor tensor = curveDecimationResult.result();
      assertTrue(tensor.length() < 100);
      tensor.set(Scalar::zero, Tensor.ALL, Tensor.ALL);
      Chop.NONE.requireAllZero(tensor);
      assertEquals(matrix, copy);
      // ---
      Tensor errors = curveDecimationResult.errors();
      Scalar max = Norm.INFINITY.ofVector(errors);
      System.out.println(max);
    }
  }

  private static final Tensor WEIGHTS1 = Tensors.fromString("{0.3[m^-1], 0.3[m^-1], 1.6}").unmodifiable();

  private static Tensor log1(Tensor xya) {
    return WEIGHTS1.pmul(Se2CoveringExponential.INSTANCE.log(xya));
  }

  private static final Tensor WEIGHTS2 = Tensors.fromString("{3.3[m^-1], 3.3[m^-1], 0.2}").unmodifiable();

  private static Tensor log2(Tensor xya) {
    return WEIGHTS2.pmul(Se2CoveringExponential.INSTANCE.log(xya));
  }

  public void testQuantity() throws ClassNotFoundException, IOException {
    GokartPoseData gokartPoseData = GokartPoseDataV2.RACING_DAY;
    String name = RandomChoice.of(gokartPoseData.list());
    Tensor matrix = Tensor.of(gokartPoseData.getPose(name, 2000).stream() //
        .map(xya -> Tensors.of( //
            Quantity.of(xya.Get(0), "m"), //
            Quantity.of(xya.Get(1), "m"), //
            xya.Get(2))));
    Tensor tensor1 = //
        Serialization.copy(CurveDecimation.of(Se2Group.INSTANCE, Se2CurveDecimationTest::log1, RealScalar.of(0.3))) //
            .apply(matrix);
    Tensor tensor2 = //
        Serialization.copy(CurveDecimation.of(Se2Group.INSTANCE, Se2CurveDecimationTest::log2, RealScalar.of(0.3))) //
            .apply(matrix);
    assertFalse(Dimensions.of(tensor1).equals(Dimensions.of(tensor2)));
  }
}
