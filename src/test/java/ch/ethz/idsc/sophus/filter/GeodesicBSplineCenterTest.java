// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.crv.spline.BSplineLimitMask;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class GeodesicBSplineCenterTest extends TestCase {
  private static final TensorUnaryOperator TENSOR_UNARY_OPERATOR = //
      GeodesicCenter.of(RnGeodesic.INSTANCE, BSplineLimitMask.FUNCTION);

  public void testSimple3() {
    Tensor tensor = TENSOR_UNARY_OPERATOR.apply(Tensors.vector(1, 2, 3));
    assertEquals(tensor, RealScalar.of(2));
  }

  public void testSimple5() {
    Tensor tensor = TENSOR_UNARY_OPERATOR.apply(Tensors.vector(1, 2, 3, 4, 5));
    assertEquals(tensor, RealScalar.of(3));
  }

  public void testAdvanced5() {
    Tensor tensor = TENSOR_UNARY_OPERATOR.apply(Tensors.vector(3, 2, 3, 4, 5));
    assertEquals(tensor, RationalScalar.of(181, 60));
  }

  public void testEvenFail() {
    try {
      TENSOR_UNARY_OPERATOR.apply(Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarFail() {
    try {
      TENSOR_UNARY_OPERATOR.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
