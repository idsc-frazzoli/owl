// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
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
    TensorUnaryOperator tensorUnaryOperator = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
    Tensor refined = Tensor.of(control.stream().map(tensorUnaryOperator));
    assertEquals(refined.get(3), Tensors.vector(3.0, 3.0, 0.0));
  }

  public void testRotation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 0, 1);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor mask = Tensors.vector(.5, .5, .5);
    Tensor control = Tensors.of(p, q, r, s);
    TensorUnaryOperator tensorUnaryOperator = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
    Tensor refined = Tensor.of(control.stream().map(tensorUnaryOperator));
    assertEquals(refined.get(3), Tensors.vector(0.0, 0.0, 3.0));
  }

  public void testCombined() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 1);
    Tensor r = q.add(q);
    Tensor s = r.add(q);
    Tensor mask = Tensors.vector(.5, .5, .5);
    Tensor control = Tensors.of(p, q, r, s);
    TensorUnaryOperator tensorUnaryOperator = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
    Tensor refined = Tensor.of(control.stream().map(tensorUnaryOperator));
    Chop._12.requireClose(refined.get(3), Tensors.vector(2.1089736636445733, 3.138480364906712, 3.0));
  }

  public void testIIR1() {
    Scalar alpha = RationalScalar.of(1, 3);
    Tensor mask = Tensors.of(alpha, RealScalar.ONE.subtract(alpha), alpha);
    GeodesicIIR1Filter geodesicIIR1Filter = new GeodesicIIR1Filter(RnGeodesic.INSTANCE, alpha);
    GeodesicIIRnFilter geodesicIIRnFilter = new GeodesicIIRnFilter(RnGeodesic.INSTANCE, mask);
    Tensor p0 = Tensors.vector(1, 2);
    Tensor r0_1 = geodesicIIR1Filter.apply(p0);
    Tensor r0_n = geodesicIIRnFilter.apply(p0);
    System.out.println(r0_1);
    System.out.println(r0_n);
    Tensor p1 = Tensors.vector(6, 3);
    Tensor r1_1 = geodesicIIR1Filter.apply(p1);
    Tensor r1_n = geodesicIIRnFilter.apply(p1);
    System.out.println(r1_1);
    System.out.println(r1_n);
  }
}