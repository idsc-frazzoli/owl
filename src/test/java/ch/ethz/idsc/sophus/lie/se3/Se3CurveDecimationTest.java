// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.so3.So3Exponential;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class Se3CurveDecimationTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(Se3CurveDecimation.of(RealScalar.of(.3)));
    Tensor p = Se3Matrix.of(So3Exponential.INSTANCE.exp(Tensors.vector(.1, -.2, -.3)), Tensors.vector(4, 3, 7));
    // Se3GroupElement pe = new Se3GroupElement(p);
    Tensor q = Se3Matrix.of(So3Exponential.INSTANCE.exp(Tensors.vector(.2, .3, -.1)), Tensors.vector(1, 2, 5));
    // Se3GroupElement qe = new Se3GroupElement(q);
    ScalarTensorFunction scalarTensorFunction = Se3Geodesic.INSTANCE.curve(p, q);
    Tensor m1 = scalarTensorFunction.apply(RealScalar.of(.3));
    Tensor m2 = scalarTensorFunction.apply(RealScalar.of(.8));
    Tensor curve = Tensors.of(p, m1, m2, q);
    Tensor tensor = tensorUnaryOperator.apply(curve);
    assertEquals(tensor.length(), 2);
    assertEquals(tensor, Tensors.of(p, q));
  }
}
