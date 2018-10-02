// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.IOException;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BSpline4CurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = Nest.of(curveSubdivision::cyclic, tensor, 1);
    assertTrue(ExactScalarQ.all(actual));
    assertEquals(actual.extract(0, 3), Tensors.fromString("{{5/8, -1/4}, {5/8, 1/4}, {1/4, 5/8}}"));
  }

  public void test2Lo() {
    CurveSubdivision curveSubdivision = //
        BSpline4CurveSubdivision.split3(RnGeodesic.INSTANCE, RationalScalar.of(1, 6));
    Tensor tensor = curveSubdivision.string(UnitVector.of(5, 2));
    assertEquals(tensor, Tensors.fromString("{0, 1/16, 5/16, 5/8, 5/8, 5/16, 1/16, 0}"));
  }

  public void test2Hi() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.split2(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(UnitVector.of(5, 2));
    assertEquals(tensor, Tensors.fromString("{0, 1/16, 5/16, 5/8, 5/8, 5/16, 1/16, 0}"));
  }

  public void testCyclic() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1, 2, 3);
    Tensor string = curveSubdivision.cyclic(vector);
    assertEquals(string, Tensors.fromString("{1, 1/2, 3/4, 5/4, 7/4, 9/4, 5/2, 2}"));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testString() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1, 2, 3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.fromString("{1/4, 3/4, 5/4, 7/4, 9/4, 11/4}"));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testStringTwo() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.fromString("{1/4, 3/4}"));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testStringOne() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.vector(3));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testScalarFail() {
    CurveSubdivision subdivision = BSpline4CurveSubdivision.of(Se2Geodesic.INSTANCE);
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
