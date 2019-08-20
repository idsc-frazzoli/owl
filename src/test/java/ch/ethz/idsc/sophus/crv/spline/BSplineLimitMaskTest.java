// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class BSplineLimitMaskTest extends TestCase {
  public void testLimitMask() {
    assertEquals(BSplineLimitMask.FUNCTION.apply(0 * 2 + 1), Tensors.fromString("{1}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(1 * 2 + 1), Tensors.fromString("{1/6, 2/3, 1/6}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(2 * 2 + 1), Tensors.fromString("{1/120, 13/60, 11/20, 13/60, 1/120}"));
    assertEquals(BSplineLimitMask.FUNCTION.apply(3 * 2 + 1), Tensors.fromString("{1/5040, 1/42, 397/1680, 151/315, 397/1680, 1/42, 1/5040}"));
  }

  public void testEvenFail() {
    for (int i = 0; i < 10; ++i)
      try {
        BSplineLimitMask.FUNCTION.apply(i * 2);
        fail();
      } catch (Exception exception) {
        // ---
      }
  }

  public void testNegativeFail() {
    for (int i = 1; i < 10; ++i)
      try {
        BSplineLimitMask.FUNCTION.apply(-i);
        fail();
      } catch (Exception exception) {
        // ---
      }
  }

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

  public void testEvenVectorFail() {
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
