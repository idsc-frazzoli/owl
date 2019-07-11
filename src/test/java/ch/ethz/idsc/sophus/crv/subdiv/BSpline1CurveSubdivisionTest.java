// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.IOException;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid1;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
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

public class BSpline1CurveSubdivisionTest extends TestCase {
  public void testCyclic() {
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = curveSubdivision.cyclic(tensor);
    ExactTensorQ.require(actual);
    Tensor expected = Tensors.fromString("{{1, 0}, {1/2, 1/2}, {0, 1}, {-1/2, 1/2}, {-1, 0}, {-1/2, -1/2}, {0, -1}, {1/2, -1/2}}");
    assertEquals(expected, actual);
  }

  public void testString() {
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor string = curveSubdivision.string(Tensors.fromString("{{0, 10}, {1, 12}}"));
    assertEquals(string, Tensors.fromString("{{0, 10}, {1/2, 11}, {1, 12}}"));
    ExactTensorQ.require(string);
  }

  public void testStringTwo() {
    Tensor curve = Tensors.vector(0, 1);
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{0, 1/2, 1}"));
    ExactTensorQ.require(refined);
  }

  public void testStringRange() {
    int length = 9;
    Tensor curve = Range.of(0, length + 1);
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Subdivide.of(0, length, length * 2));
    ExactTensorQ.require(refined);
  }

  public void testStringOne() {
    Tensor curve = Tensors.vector(8);
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{8}"));
    ExactTensorQ.require(refined);
  }

  public void testStringEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertTrue(Tensors.isEmpty(refined));
    ExactTensorQ.require(refined);
  }

  public void testCyclicEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.cyclic(curve);
    assertTrue(Tensors.isEmpty(refined));
    ExactTensorQ.require(refined);
  }

  public void testCirclePoints() {
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
    for (int n = 3; n < 10; ++n) {
      Tensor tensor = curveSubdivision.cyclic(CirclePoints.of(n));
      Tensor filter = Tensor.of(IntStream.range(0, tensor.length()) //
          .filter(i -> i % 2 == 0) //
          .mapToObj(tensor::get));
      assertEquals(filter, CirclePoints.of(n));
    }
  }

  public void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(Clothoid1.INSTANCE);
    assertEquals(curveSubdivision.cyclic(singleton), singleton);
    assertEquals(curveSubdivision.string(singleton), singleton);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testScalarFail() {
    CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE);
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
