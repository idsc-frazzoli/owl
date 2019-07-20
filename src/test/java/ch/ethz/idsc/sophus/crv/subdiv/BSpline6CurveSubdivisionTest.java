// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class BSpline6CurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = //
        BSpline6CurveSubdivision.of(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.cyclic(UnitVector.of(5, 0));
    assertEquals(tensor, //
        Tensors.fromString("{35/64, 21/64, 7/64, 1/64, 0, 0, 1/64, 7/64, 21/64, 35/64}"));
    ExactTensorQ.require(tensor);
  }

  public void testEmpty() {
    Tensor curve = Tensors.vector();
    CurveSubdivision curveSubdivision = BSpline6CurveSubdivision.of(RnGeodesic.INSTANCE);
    assertEquals(curveSubdivision.cyclic(curve), Tensors.empty());
  }

  public void testSingleton() {
    Tensor singleton = Tensors.of(Tensors.vector(1, 2, 3));
    CurveSubdivision curveSubdivision = BSpline6CurveSubdivision.of(Clothoid.INSTANCE);
    assertEquals(curveSubdivision.cyclic(singleton), singleton);
  }
}
