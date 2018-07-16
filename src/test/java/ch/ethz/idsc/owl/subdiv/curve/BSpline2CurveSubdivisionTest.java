// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.IOException;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BSpline2CurveSubdivisionTest extends TestCase {
  public void testSimple() {
    BSpline2CurveSubdivision subdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = subdivision.cyclic(tensor);
    assertTrue(ExactScalarQ.all(actual));
    Tensor expected = Tensors.fromString("{{3/4, 1/4}, {1/4, 3/4}, {-1/4, 3/4}, {-3/4, 1/4}, {-3/4, -1/4}, {-1/4, -3/4}, {1/4, -3/4}, {3/4, -1/4}}");
    assertEquals(expected, actual);
  }

  public void testString() {
    BSpline2CurveSubdivision subdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor string = subdivision.string(Tensors.vector(10, 11.));
    assertEquals(string, Tensors.vector(10.25, 10.75));
    assertFalse(ExactScalarQ.all(string));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testScalarFail() {
    CurveSubdivision subdivision = new BSpline2CurveSubdivision(Se2Geodesic.INSTANCE);
    try {
      subdivision.string(RealScalar.ONE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      subdivision.cyclic(RealScalar.ONE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
