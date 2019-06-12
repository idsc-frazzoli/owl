// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidCurve;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LaneRiesenfeldCurveSubdivisionTest extends TestCase {
  public void testDeg1() {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(RnGeodesic.INSTANCE, 1);
    Tensor string = curveSubdivision.string(Tensors.vector(1, 2, 3, 4));
    assertEquals(string, Tensors.fromString("{1, 3/2, 2, 5/2, 3, 7/2, 4}"));
    ExactTensorQ.require(string);
  }

  public void testDeg2() {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(RnGeodesic.INSTANCE, 2);
    Tensor string = curveSubdivision.string(Tensors.vector(1, 2, 3, 4));
    assertEquals(string, Tensors.fromString("{5/4, 7/4, 9/4, 11/4, 13/4, 15/4}"));
    ExactTensorQ.require(string);
  }

  public void testDeg3() {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(RnGeodesic.INSTANCE, 3);
    Tensor string = curveSubdivision.string(Tensors.vector(1, 2, 3, 4));
    assertEquals(string, Tensors.fromString("{1, 3/2, 2, 5/2, 3, 7/2, 4}"));
    ExactTensorQ.require(string);
  }

  public void testDeg4() {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(RnGeodesic.INSTANCE, 4);
    Tensor string = curveSubdivision.string(Tensors.vector(1, 2, 3, 4));
    assertEquals(string, Tensors.fromString("{5/4, 7/4, 9/4, 11/4, 13/4, 15/4}"));
    ExactTensorQ.require(string);
  }

  public void testDeg5() {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(RnGeodesic.INSTANCE, 5);
    Tensor string = curveSubdivision.string(Tensors.vector(1, 2, 3, 4));
    assertEquals(string, Tensors.fromString("{1, 3/2, 2, 5/2, 3, 7/2, 4}"));
    ExactTensorQ.require(string);
  }

  public void testCyc2() {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(RnGeodesic.INSTANCE, 2);
    Tensor cyclic = curveSubdivision.cyclic(Tensors.vector(1, 2, 3, 4));
    assertEquals(cyclic, Tensors.fromString("{5/4, 7/4, 9/4, 11/4, 13/4, 15/4, 13/4, 7/4}"));
    ExactTensorQ.require(cyclic);
  }

  public void testCyc3() {
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(RnGeodesic.INSTANCE, 3);
    CurveSubdivision curveSubdivisiom = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    assertEquals(curveSubdivisiom.cyclic(tensor), Tensors.fromString("{3/2, 3/2, 2, 5/2, 3, 7/2, 7/2, 5/2}"));
    Tensor cyclic = curveSubdivision.cyclic(tensor);
    assertEquals(curveSubdivisiom.cyclic(tensor), cyclic);
    ExactTensorQ.require(cyclic);
  }

  public void testEmpty() {
    for (int degree = 1; degree < 4; ++degree) {
      CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(ClothoidCurve.INSTANCE, degree);
      assertEquals(curveSubdivision.cyclic(Tensors.empty()), Tensors.empty());
      assertEquals(curveSubdivision.string(Tensors.empty()), Tensors.empty());
    }
  }

  public void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    for (int degree = 1; degree < 4; ++degree) {
      CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(ClothoidCurve.INSTANCE, degree);
      assertEquals(curveSubdivision.cyclic(singleton), singleton);
      assertEquals(curveSubdivision.string(singleton), singleton);
    }
  }
}
