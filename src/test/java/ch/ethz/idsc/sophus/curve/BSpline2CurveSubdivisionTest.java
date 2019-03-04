// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.IOException;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BSpline2CurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = BSpline2CurveSubdivision.of(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = curveSubdivision.cyclic(tensor);
    assertTrue(ExactScalarQ.all(actual));
    Tensor expected = Tensors.fromString("{{3/4, 1/4}, {1/4, 3/4}, {-1/4, 3/4}, {-3/4, 1/4}, {-3/4, -1/4}, {-1/4, -3/4}, {1/4, -3/4}, {3/4, -1/4}}");
    assertEquals(expected, actual);
  }

  public void testString() {
    CurveSubdivision curveSubdivision = BSpline2CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor string = curveSubdivision.string(Tensors.vector(10, 11.));
    assertEquals(string, Tensors.vector(10.25, 10.75));
    assertFalse(ExactScalarQ.all(string));
  }

  public void testStringTwo() {
    Tensor curve = Tensors.vector(0, 1);
    CurveSubdivision curveSubdivision = BSpline2CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{1/4, 3/4}"));
    assertTrue(ExactScalarQ.all(refined));
  }

  public void testStringOne() {
    Tensor curve = Tensors.vector(1);
    CurveSubdivision curveSubdivision = BSpline2CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{1}"));
    assertTrue(ExactScalarQ.all(refined));
  }

  public void testStringEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = BSpline2CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertTrue(Tensors.isEmpty(refined));
    assertTrue(ExactScalarQ.all(refined));
  }

  public void testStringRange() {
    int length = 9;
    Tensor curve = Range.of(0, length + 1);
    CurveSubdivision curveSubdivision = BSpline2CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    Tensor tensor = Subdivide.of(0, length, length * 2).map(scalar -> scalar.add(RationalScalar.of(1, 4)));
    assertEquals(refined, tensor.extract(0, tensor.length() - 1));
    assertTrue(ExactScalarQ.all(refined));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = BSpline2CurveSubdivision.of(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testScalarFail() {
    CurveSubdivision curveSubdivision = BSpline2CurveSubdivision.of(Se2Geodesic.INSTANCE);
    try {
      curveSubdivision.string(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      curveSubdivision.cyclic(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
