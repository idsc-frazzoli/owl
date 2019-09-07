// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class Se2CurveDecimationTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator tensorUnaryOperator = Se2CurveDecimation.of(RealScalar.of(.3));
    Tensor p = Tensors.vector(4, 3, 7);
    // Se3GroupElement pe = new Se3GroupElement(p);
    Tensor q = Tensors.vector(1, 2, 5);
    // Se3GroupElement qe = new Se3GroupElement(q);
    ScalarTensorFunction scalarTensorFunction = Se2Geodesic.INSTANCE.curve(p, q);
    Tensor m1 = scalarTensorFunction.apply(RealScalar.of(.3));
    Tensor m2 = scalarTensorFunction.apply(RealScalar.of(.8));
    Tensor curve = Tensors.of(p, m1, m2, q);
    Tensor tensor = tensorUnaryOperator.apply(curve);
    assertEquals(tensor.length(), 2);
    assertEquals(tensor, Tensors.of(p, q));
  }
}
