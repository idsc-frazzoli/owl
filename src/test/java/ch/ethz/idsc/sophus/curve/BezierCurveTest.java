// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import junit.framework.TestCase;

public class BezierCurveTest extends TestCase {
  public void testSimple() {
    BezierCurve bezierCurve = new BezierCurve(RnGeodesic.INSTANCE);
    ScalarTensorFunction function = bezierCurve.evaluation(Tensors.fromString("{{0, 1}, {1, 0}, {2, 1}}"));
    Tensor tensor = function.apply(RationalScalar.of(1, 4));
    assertEquals(tensor, Tensors.fromString("{1/2, 5/8}"));
    assertTrue(ExactScalarQ.all(tensor));
  }
}
