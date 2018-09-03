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
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class HormannSabinCurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = HormannSabinCurveSubdivision.of(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = Nest.of(curveSubdivision::cyclic, tensor, 1);
    assertTrue(ExactScalarQ.all(actual));
    Tensor p = tensor.get(3);
    Tensor q = tensor.get(0);
    Tensor r = tensor.get(1);
    Tensor weights = Tensors.fromString("{1/4-3/32, 3/4+6/32, -3/32}");
    assertEquals(weights.dot(Tensors.of(p, q, r)), actual.get(0));
    assertEquals(weights.dot(Tensors.of(r, q, p)), actual.get(1));
  }

  public void testString() {
    CurveSubdivision curveSubdivision = HormannSabinCurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1, 2, 3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.fromString("{1/4, 3/4, 5/4, 7/4, 9/4, 11/4}"));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testStringTwo() {
    CurveSubdivision curveSubdivision = HormannSabinCurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.fromString("{1/4, 3/4}"));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testStringOne() {
    CurveSubdivision curveSubdivision = HormannSabinCurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.vector(3));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = HormannSabinCurveSubdivision.of(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testScalarFail() {
    CurveSubdivision subdivision = HormannSabinCurveSubdivision.of(Se2Geodesic.INSTANCE);
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
