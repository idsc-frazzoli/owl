// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.IOException;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.num.Rationalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class FourPointCurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = Nest.of(curveSubdivision::cyclic, tensor, 1);
    ExactTensorQ.require(actual);
    assertEquals(actual, Tensors.fromString("{{1, 0}, {5/8, 5/8}, {0, 1}, {-5/8, 5/8}, {-1, 0}, {-5/8, -5/8}, {0, -1}, {5/8, -5/8}}"));
  }

  public void testString() {
    CurveSubdivision curveSubdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1, 2, 3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Subdivide.of(0, 3, 6));
    ExactTensorQ.require(string);
  }

  public void testStringTwo() {
    CurveSubdivision curveSubdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Subdivide.of(0, 1, 2));
    ExactTensorQ.require(string);
  }

  public void testStringOne() {
    CurveSubdivision curveSubdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(3);
    Tensor string = curveSubdivision.string(vector);
    assertEquals(string, Tensors.vector(3));
    ExactTensorQ.require(string);
  }

  public void testSimple1() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{0,1}}");
    TensorUnaryOperator curveSubdivision = //
        new FourPointCurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
    Tensor n1 = Nest.of(curveSubdivision, curve, 1);
    assertEquals(n1.get(0), Array.zeros(2));
    assertEquals(n1.get(1), Tensors.fromString("{9/16, -1/8}"));
    assertEquals(n1.get(2), UnitVector.of(2, 0));
    assertEquals(n1.get(3), Tensors.fromString("{9/16, 9/16}"));
  }

  public void testCyclic() {
    CurveSubdivision curveSubdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    for (int n = 3; n < 10; ++n) {
      Tensor tensor = curveSubdivision.cyclic(CirclePoints.of(n));
      Tensor filter = Tensor.of(IntStream.range(0, tensor.length()) //
          .filter(i -> i % 2 == 0) //
          .mapToObj(tensor::get));
      assertEquals(filter, CirclePoints.of(n));
    }
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = new FourPointCurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }

  public void testScalarFail() {
    CurveSubdivision subdivision = new FourPointCurveSubdivision(Se2Geodesic.INSTANCE);
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
