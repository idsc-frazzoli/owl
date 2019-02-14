//code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicIIRnFilterNEWTest extends TestCase {
  public void testTranslation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 0);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor control = Tensors.of(p, q, r, s);
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor refined = GeodesicIIRnFilterNEW.of(tensorUnaryOperator, geodesicInterface, 2, RealScalar.of(Math.random())).apply(control);
    assertEquals(refined.get(3), Tensors.vector(3.0, 3.0, 0.0));
  }

  public void testRotation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 0, 1);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor control = Tensors.of(p, q, r, s);
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor refined = GeodesicIIRnFilterNEW.of(tensorUnaryOperator, geodesicInterface, 2, RealScalar.of(Math.random())).apply(control);
    assertEquals(refined.get(3), Tensors.vector(0.0, 0.0, 3.0));
  }

  public void testCombined() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 1);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor control = Tensors.of(p, q, r, s);
    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, SmoothingKernel.GAUSSIAN);
    Tensor refined = GeodesicIIRnFilterNEW.of(tensorUnaryOperator, geodesicInterface, 2, RealScalar.of(.5)).apply(control);
    Chop._12.requireClose(refined.get(3), Tensors.vector(1.7680545946869155, 3.0641742929076536, 3.0));
  }
}
