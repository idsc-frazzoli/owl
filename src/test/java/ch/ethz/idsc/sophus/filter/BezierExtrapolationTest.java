// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BezierExtrapolationTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator tensorUnaryOperator = BezierExtrapolation.of(RnGeodesic.INSTANCE);
    for (int index = 2; index < 10; ++index)
      assertEquals(tensorUnaryOperator.apply(Range.of(0, index)), RealScalar.of(index));
  }

  public void testCircle2() {
    TensorUnaryOperator tensorUnaryOperator = BezierExtrapolation.of(Se2Geodesic.INSTANCE);
    Tensor tensor = tensorUnaryOperator.apply(Tensors.fromString("{{0, 0, 0}, {1, 1, " + Math.PI / 2 + "}}"));
    Tensor result = Tensors.vector(0, 2, Math.PI);
    Chop._14.requireClose(tensor, result);
  }

  public void testCircle3() {
    TensorUnaryOperator tensorUnaryOperator = BezierExtrapolation.of(Se2Geodesic.INSTANCE);
    Tensor tensor = tensorUnaryOperator.apply(Tensors.fromString("{{0, 0, 0}, {1, 1, " + Math.PI / 2 + "}, {0, 2, " + Math.PI + "}}"));
    Tensor result = Tensors.vector(-1, 1, Math.PI * 3 / 2);
    Chop._14.requireClose(tensor, result);
  }

  public void testMask() {
    assertEquals(BezierExtrapolation.mask(2), Tensors.fromString("{-1, 2}"));
    assertEquals(BezierExtrapolation.mask(3), Tensors.fromString("{1/4, -3/2, 9/4}"));
    assertEquals(BezierExtrapolation.mask(4), Tensors.fromString("{-1/27, 4/9, -16/9, 64/27}"));
    for (int n = 2; n < 10; ++n) {
      Tensor mask = BezierExtrapolation.mask(n);
      assertEquals(Total.of(mask), RealScalar.ONE);
      ExactScalarQ.requireAll(mask);
    }
  }

  public void testFailScalar() {
    TensorUnaryOperator tensorUnaryOperator = BezierExtrapolation.of(RnGeodesic.INSTANCE);
    try {
      tensorUnaryOperator.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailEmpty() {
    TensorUnaryOperator tensorUnaryOperator = BezierExtrapolation.of(Se2Geodesic.INSTANCE);
    try {
      tensorUnaryOperator.apply(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailSe2_1() {
    TensorUnaryOperator tensorUnaryOperator = BezierExtrapolation.of(Se2Geodesic.INSTANCE);
    try {
      tensorUnaryOperator.apply(Tensors.vector(1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailSe2() {
    TensorUnaryOperator tensorUnaryOperator = BezierExtrapolation.of(Se2Geodesic.INSTANCE);
    try {
      tensorUnaryOperator.apply(Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
