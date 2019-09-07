// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CurveDecimationTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator tensorUnaryOperator = Se2CurveDecimation.of(RealScalar.of(.3));
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

  public void testGokart() {
    GokartPoseData gokartPoseData = GokartPoseDataV2.RACING_DAY;
    TensorUnaryOperator tensorUnaryOperator = Se2CurveDecimation.of(RealScalar.of(.3));
    for (String name : gokartPoseData.list()) {
      Tensor matrix = gokartPoseData.getPose(name, 2000);
      Tensor copy = matrix.copy();
      Tensor tensor = tensorUnaryOperator.apply(matrix);
      assertTrue(tensor.length() < 100);
      tensor.set(Scalar::zero, Tensor.ALL, Tensor.ALL);
      Chop.NONE.requireAllZero(tensor);
      assertEquals(matrix, copy);
    }
  }
}
