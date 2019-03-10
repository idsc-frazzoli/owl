// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.IOException;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.num.Rationalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BSpline2CurveSubdivisionTest extends TestCase {
  private static final CurveSubdivision CURVE_SUBDIVISION = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);

  public void testCyclic() {
    Tensor cyclic = CURVE_SUBDIVISION.cyclic(Tensors.vector(1, 2, 3, 4));
    assertEquals(cyclic, Tensors.fromString("{5/4, 7/4, 9/4, 11/4, 13/4, 15/4, 13/4, 7/4}"));
  }

  public void testSimple() {
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = CURVE_SUBDIVISION.cyclic(tensor);
    ExactTensorQ.require(actual);
    Tensor expected = Tensors.fromString("{{3/4, 1/4}, {1/4, 3/4}, {-1/4, 3/4}, {-3/4, 1/4}, {-3/4, -1/4}, {-1/4, -3/4}, {1/4, -3/4}, {3/4, -1/4}}");
    assertEquals(expected, actual);
  }

  public void testString() {
    Tensor string = CURVE_SUBDIVISION.string(Tensors.vector(10, 11.));
    assertEquals(string, Tensors.vector(10.25, 10.75));
    assertFalse(ExactTensorQ.of(string));
  }

  public void testStringTwo() {
    Tensor curve = Tensors.vector(0, 1);
    Tensor refined = CURVE_SUBDIVISION.string(curve);
    assertEquals(refined, Tensors.fromString("{1/4, 3/4}"));
    ExactTensorQ.require(refined);
  }

  public void testStringOne() {
    Tensor curve = Tensors.vector(1);
    Tensor refined = CURVE_SUBDIVISION.string(curve);
    assertEquals(refined, Tensors.fromString("{1}"));
    ExactTensorQ.require(refined);
  }

  public void testStringEmpty() {
    Tensor curve = Tensors.vector();
    Tensor refined = CURVE_SUBDIVISION.string(curve);
    assertTrue(Tensors.isEmpty(refined));
    ExactTensorQ.require(refined);
  }

  public void testStringRange() {
    int length = 9;
    Tensor curve = Range.of(0, length + 1);
    Tensor refined = CURVE_SUBDIVISION.string(curve);
    Tensor tensor = Subdivide.of(0, length, length * 2).map(scalar -> scalar.add(RationalScalar.of(1, 4)));
    assertEquals(refined, tensor.extract(0, tensor.length() - 1));
    ExactTensorQ.require(refined);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testScalarFail() {
    CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(Se2Geodesic.INSTANCE);
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
