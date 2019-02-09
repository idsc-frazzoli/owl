// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BezierFunctionTest extends TestCase {
  public void testSimple() {
    Tensor control = Tensors.fromString("{{0, 1}, {1, 0}, {2, 1}}");
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(RnGeodesic.INSTANCE, control);
    Tensor tensor = scalarTensorFunction.apply(RationalScalar.of(1, 4));
    assertEquals(tensor, Tensors.fromString("{1/2, 5/8}"));
    assertTrue(ExactScalarQ.all(tensor));
  }

  public void testRn() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 1}, {2, 0}, {3, 1}}");
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(RnGeodesic.INSTANCE, control);
    Scalar scalar = RationalScalar.of(1, 4);
    Tensor tensor = scalarTensorFunction.apply(scalar);
    assertEquals(tensor, Tensors.fromString("{3/4, 7/16}"));
    assertTrue(ExactScalarQ.all(tensor));
  }

  public void testSe2Covering() {
    Tensor control = Tensors.fromString("{{0, 0, 0}, {1, 0, 1/2}, {2, 0.4, 2/5}}");
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(Se2CoveringGeodesic.INSTANCE, control);
    Scalar scalar = RationalScalar.of(1, 4);
    Tensor tensor = scalarTensorFunction.apply(scalar);
    assertEquals(tensor.Get(2), RationalScalar.of(17, 80));
    assertTrue(ExactScalarQ.all(tensor.Get(2)));
  }

  public void testOutsideFail() {
    Tensor control = Tensors.fromString("{{0, 0, 0}, {1, 0, 1/2}, {2, 0.4, 2/5}}");
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(Se2CoveringGeodesic.INSTANCE, control);
    Scalar scalar = RationalScalar.of(-1, 4);
    Tensor tensor = scalarTensorFunction.apply(scalar);
    Chop._12.requireClose(tensor, Tensors.vector(-0.45359613406197646, 0.22282532025418184, -23 / 80.));
    ExactScalarQ.require(tensor.Get(2));
  }
}
