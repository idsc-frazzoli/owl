// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class GeodesicFIR3FilterTest extends TestCase {
  public void testTranslation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 0);
    Tensor r = Tensors.vector(2, 2, 0);
    Scalar alpha = RealScalar.of(0.5);
    Scalar beta = RealScalar.of(Math.random());
    Tensor control = Tensors.of(p, q, r);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicFIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    // geodesicCenterFilter.apply(p);
    // geodesicCenterFilter.apply(q);
    // geodesicCenterFilter.apply(r);
    // Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    // assertEquals(Dimensions.of(refined), Arrays.asList(3, 3));
    // assertEquals(refined.get(1), Tensors.vector(0.5, 0.5, 0.0));
    // Chop._12.requireClose(refined.get(2), Tensors.vector(1.8333333333333333, 1.8333333333333333, 0.0));
  }

  public void testRotation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 0, 1);
    Tensor r = Tensors.vector(0, 0, 2);
    Scalar alpha = RealScalar.of(0.5);
    Scalar beta = RealScalar.of(Math.random());
    Tensor control = Tensors.of(p, q, r);
    // TensorUnaryOperator geodesicCenterFilter = new GeodesicFIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    // Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    // assertEquals(refined.get(1), Tensors.vector(0, 0, 0.5));
    // Chop._12.requireClose(refined.get(2), Tensors.vector(0, 0, 1.8333333333333333));
  }

  public void testCombined() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 1);
    Tensor r = Tensors.vector(2, 2, 2);
    Scalar alpha = RealScalar.of(0.5);
    Scalar beta = RealScalar.of(Math.random());
    Tensor control = Tensors.of(p, q, r);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicFIR3Filter(Se2Geodesic.INSTANCE, alpha, beta);
    // Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    // Chop._12.requireClose(refined.get(1), Tensors.vector(0.6276709606105183, 0.3723290393894818, 0.5));
    // Chop._12.requireClose(refined.get(2), Tensors.vector(1.4783775279675098, 1.9383316968973154, 1.8333333333333333));
  }
}
