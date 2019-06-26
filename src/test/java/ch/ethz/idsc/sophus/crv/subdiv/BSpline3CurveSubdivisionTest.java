// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid1;
import ch.ethz.idsc.sophus.hs.r3s2.R3S2Geodesic;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.num.Rationalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BSpline3CurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = Nest.of(curveSubdivision::cyclic, tensor, 1);
    ExactTensorQ.require(actual);
    Tensor expected = Tensors.fromString("{{3/4, 0}, {1/2, 1/2}, {0, 3/4}, {-1/2, 1/2}, {-3/4, 0}, {-1/2, -1/2}, {0, -3/4}, {1/2, -1/2}}");
    assertEquals(expected, actual);
  }

  public void testString() {
    Tensor curve = Tensors.vector(0, 1, 2, 3);
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{0, 1/2, 1, 3/2, 2, 5/2, 3}"));
    ExactTensorQ.require(refined);
  }

  public void testStringTwo() {
    Tensor curve = Tensors.vector(0, 1);
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{0, 1/2, 1}"));
    ExactTensorQ.require(refined);
  }

  public void testStringOne() {
    Tensor curve = Tensors.vector(1);
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor refined = curveSubdivision.string(curve);
    assertEquals(refined, Tensors.fromString("{1}"));
    ExactTensorQ.require(refined);
  }

  public void testEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);
    assertEquals(curveSubdivision.string(curve), Tensors.empty());
    assertEquals(curveSubdivision.cyclic(curve), Tensors.empty());
  }

  public void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(Clothoid1.INSTANCE);
    assertEquals(curveSubdivision.cyclic(singleton), singleton);
    assertEquals(curveSubdivision.string(singleton), singleton);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testR3S2() {
    Tensor tensor = Subdivide.of(-0.5, 0.8, 6) //
        .map(scalar -> Tensors.of(scalar, RealScalar.of(0.3), RealScalar.ONE));
    tensor = Tensor.of(tensor.stream() //
        .map(Normalize.with(Norm._2)) //
        .map(row -> Tensors.of(row, row)));
    TensorUnaryOperator string = new BSpline3CurveSubdivision(R3S2Geodesic.INSTANCE)::string;
    Tensor apply = string.apply(tensor);
    assertEquals(Dimensions.of(apply), Arrays.asList(13, 2, 3));
  }

  public void testScalarFail() {
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(Se2Geodesic.INSTANCE);
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
