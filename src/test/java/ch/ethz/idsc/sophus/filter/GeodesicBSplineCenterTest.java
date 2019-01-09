// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class GeodesicBSplineCenterTest extends TestCase {
  public void testSimple3() {
    TensorUnaryOperator tensorUnaryOperator = new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    Tensor tensor = tensorUnaryOperator.apply(Tensors.vector(1, 2, 3));
    assertEquals(tensor, RealScalar.of(2));
  }

  public void testSimple5() {
    TensorUnaryOperator tensorUnaryOperator = new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    Tensor tensor = tensorUnaryOperator.apply(Tensors.vector(1, 2, 3, 4, 5));
    assertEquals(tensor, RealScalar.of(3));
  }

  public void testAdvanced5() {
    TensorUnaryOperator tensorUnaryOperator = new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    Tensor tensor = tensorUnaryOperator.apply(Tensors.vector(3, 2, 3, 4, 5));
    assertEquals(tensor, RationalScalar.of(464023, 154674));
  }

  public void testEvenFail() {
    TensorUnaryOperator tensorUnaryOperator = new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    try {
      tensorUnaryOperator.apply(Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarFail() {
    TensorUnaryOperator tensorUnaryOperator = new GeodesicBSplineCenter(RnGeodesic.INSTANCE);
    try {
      tensorUnaryOperator.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
