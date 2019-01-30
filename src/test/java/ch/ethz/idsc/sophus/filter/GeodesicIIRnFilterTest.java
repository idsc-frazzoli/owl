package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicIIRnFilterTest extends TestCase {
  public void testTranslation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 0);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor mask = Tensors.vector(.5, .5, .5);
    Tensor control = Tensors.of(p, q, r, s);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(refined.get(3), Tensors.vector(3.0, 3.0, 0.0));
  }

  //
  public void testRotation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 0, 1);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor mask = Tensors.vector(.5, .5, .5);
    Tensor control = Tensors.of(p, q, r, s);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    assertEquals(refined.get(3), Tensors.vector(0.0, 0.0, 3.0));
  }

  public void testCombined() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 1);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor mask = Tensors.vector(.5, .5, .5);
    Tensor control = Tensors.of(p, q, r, s);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
    Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
    Chop._12.requireClose(refined.get(3), Tensors.vector(2.1089736636445733, 3.138480364906712, 3.0));
  }
}