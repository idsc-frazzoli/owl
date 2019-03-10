// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class EightPointCurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = new EightPointCurveSubdivision(RnGeodesic.INSTANCE);
    Tensor cyclic = curveSubdivision.cyclic(UnitVector.of(10, 5));
    assertEquals(Total.of(cyclic), RealScalar.of(2));
    ExactTensorQ.require(cyclic);
    Tensor result = Tensors.fromString( //
        "{0, 0, 0, -5/2048, 0, 49/2048, 0, -245/2048, 0, 1225/2048, 1, 1225/2048, 0, -245/2048, 0, 49/2048, 0, -5/2048, 0, 0}");
    assertEquals(cyclic, result);
  }

  public void testCircle() {
    CurveSubdivision curveSubdivision = new EightPointCurveSubdivision(RnGeodesic.INSTANCE);
    for (int n = 40; n < 60; n += 3)
      Chop._09.requireClose(curveSubdivision.cyclic(CirclePoints.of(n)), CirclePoints.of(n * 2));
  }
}
