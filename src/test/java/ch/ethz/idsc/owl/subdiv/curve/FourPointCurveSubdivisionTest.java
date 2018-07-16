// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.IOException;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class FourPointCurveSubdivisionTest extends TestCase {
  public void testSimple() {
    FourPointCurveSubdivision subdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    ScalarUnaryOperator operator = Rationalize.withDenominatorLessEquals(100);
    Tensor tensor = CirclePoints.of(4).map(operator);
    Tensor actual = Nest.of(subdivision::cyclic, tensor, 1);
    assertTrue(ExactScalarQ.all(actual));
    assertEquals(actual, Tensors.fromString("{{1, 0}, {5/8, 5/8}, {0, 1}, {-5/8, 5/8}, {-1, 0}, {-5/8, -5/8}, {0, -1}, {5/8, -5/8}}"));
  }

  public void testString() {
    FourPointCurveSubdivision subdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1, 2, 3);
    Tensor string = subdivision.string(vector);
    assertEquals(string, Subdivide.of(0, 3, 6));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testStringTwo() {
    FourPointCurveSubdivision subdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(0, 1);
    Tensor string = subdivision.string(vector);
    assertEquals(string, Subdivide.of(0, 1, 2));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testStringOne() {
    FourPointCurveSubdivision subdivision = new FourPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor vector = Tensors.vector(3);
    Tensor string = subdivision.string(vector);
    assertEquals(string, Tensors.vector(3));
    assertTrue(ExactScalarQ.all(string));
  }

  public void testSimple1() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{0,1}}");
    TensorUnaryOperator subdivision = //
        new FourPointCurveSubdivision(RnGeodesic.INSTANCE)::cyclic;
    Tensor n1 = Nest.of(subdivision, curve, 1);
    assertEquals(n1.get(0), Array.zeros(2));
    assertEquals(n1.get(1), Tensors.fromString("{9/16, -1/8}"));
    assertEquals(n1.get(2), UnitVector.of(2, 0));
    assertEquals(n1.get(3), Tensors.fromString("{9/16, 9/16}"));
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
