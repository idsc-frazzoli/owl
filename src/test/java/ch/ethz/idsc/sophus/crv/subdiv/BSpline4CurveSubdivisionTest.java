// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoids;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.num.Rationalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BSpline4CurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = Nest.of(curveSubdivision::cyclic, tensor, 1);
    ExactTensorQ.require(actual);
    assertEquals(actual.extract(0, 3), Tensors.fromString("{{5/8, -1/4}, {5/8, 1/4}, {1/4, 5/8}}"));
  }

  public void test2Lo() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.split3(RnGeodesic.INSTANCE, RationalScalar.of(1, 6));
    Tensor tensor = curveSubdivision.string(UnitVector.of(5, 2));
    assertEquals(tensor, Tensors.fromString("{0, 1/16, 5/16, 5/8, 5/8, 5/16, 1/16, 0}"));
  }

  public void testSplit3() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.split3(RnGeodesic.INSTANCE);
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
    ExactTensorQ.require(string);
  }

  public void testString() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1, 2, 3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.fromString("{1/4, 3/4, 5/4, 7/4, 9/4, 11/4}"));
    ExactTensorQ.require(string);
  }

  public void testStringTwo() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.fromString("{1/4, 3/4}"));
    ExactTensorQ.require(string);
  }

  public void testStringOne() {
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.vector(3));
    ExactTensorQ.require(string);
  }

  public void testEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(RnGeodesic.INSTANCE);
    assertEquals(curveSubdivision.string(curve), Tensors.empty());
    assertEquals(curveSubdivision.cyclic(curve), Tensors.empty());
  }

  public void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    CurveSubdivision curveSubdivision = BSpline4CurveSubdivision.of(Clothoids.INSTANCE);
    assertEquals(curveSubdivision.cyclic(singleton), singleton);
    assertEquals(curveSubdivision.string(singleton), singleton);
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
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      subdivision.cyclic(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
