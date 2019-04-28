// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class BSpline5CurveSubdivisionTest extends TestCase {
  public void testCyclicMask() {
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.cyclic(Tensors.vector(1, 0, 0, 0, 0, 0, 0));
    assertEquals(tensor, Tensors.fromString( //
        "{5/8, 15/32, 3/16, 1/32, 0, 0, 0, 0, 0, 0, 0, 1/32, 3/16, 15/32}"));
  }

  public void testString() {
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    for (int length = 3; length < 7; ++length) {
      Tensor tensor = curveSubdivision.cyclic(UnitVector.of(length, 2));
      ExactTensorQ.require(tensor);
      assertEquals(Total.of(tensor), RealScalar.of(2));
    }
  }

  public void testEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    assertEquals(curveSubdivision.string(curve), Tensors.empty());
    assertEquals(curveSubdivision.cyclic(curve), Tensors.empty());
  }

  public void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(ClothoidCurve.INSTANCE);
    assertEquals(curveSubdivision.cyclic(singleton), singleton);
    assertEquals(curveSubdivision.string(singleton), singleton);
  }

  public void testTerminal() {
    CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    Clip clip = Clips.interval(1, 2);
    for (int length = 2; length < 7; ++length) {
      Tensor tensor = curveSubdivision.string(UnitVector.of(length, 0));
      ExactTensorQ.require(tensor);
      clip.requireInside(Total.of(tensor).Get());
    }
  }
}
