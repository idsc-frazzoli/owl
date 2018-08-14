// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DeCasteljauTest extends TestCase {
  public void testSimple() {
    Tensor points = Tensors.fromString("{{0, 0}, {1, 1}, {2, 0}, {3, 1}}");
    DeCasteljau curve = new DeCasteljau(RnGeodesic.INSTANCE, points);
    Scalar scalar = RationalScalar.of(1, 4);
    Tensor result = curve.apply(scalar);
    assertEquals(result, Tensors.fromString("{3/4, 7/16}"));
    assertTrue(ExactScalarQ.all(result));
  }

  public void testSe2Covering() {
    Tensor points = Tensors.fromString("{{0, 0, 0}, {1, 0, 1/2}, {2, 0.4, 2/5}}");
    DeCasteljau curve = new DeCasteljau(Se2CoveringGeodesic.INSTANCE, points);
    Scalar scalar = RationalScalar.of(1, 4);
    Tensor result = curve.apply(scalar);
    assertEquals(result.Get(2), RationalScalar.of(17, 80));
    assertTrue(ExactScalarQ.all(result.Get(2)));
  }
}
