// code by ob
package ch.ethz.idsc.sophus.filter.ga;

import java.util.Arrays;

import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicFIR2FilterTest extends TestCase {
  public void testTranslation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 0);
    Scalar alpha = RealScalar.of(0.5);
    Tensor control = Tensors.of(p, q);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicFIR2Filter(Se2Geodesic.INSTANCE, alpha);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(refined.get(1), Tensors.vector(0.5, 0.5, 0));
  }

  public void testRotation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 0, 2);
    Scalar alpha = RealScalar.of(0.5);
    Tensor control = Tensors.of(p, q);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicFIR2Filter(Se2Geodesic.INSTANCE, alpha);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(refined.get(1), Tensors.vector(0, 0, 1));
  }

  public void testCombined() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 1);
    Scalar alpha = RealScalar.of(0.5);
    Tensor control = Tensors.of(p, q);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicFIR2Filter(Se2Geodesic.INSTANCE, alpha);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(Dimensions.of(refined), Arrays.asList(2, 3));
    Chop._12.requireClose(refined.get(1), Tensors.vector(0.6276709606105183, 0.3723290393894818, 0.5));
  }
}
