// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class DualC2FourPointCurveSubdivisionTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = DualC2FourPointCurveSubdivision.cubic(RnGeodesic.INSTANCE);
    Tensor cyclic = curveSubdivision.cyclic(UnitVector.of(10, 1));
    assertEquals(Total.of(cyclic), RealScalar.of(2));
  }

  public void testSpecs() {
    CurveSubdivision curveSubdivision = DualC2FourPointCurveSubdivision.cubic(RnGeodesic.INSTANCE);
    Tensor cyclic = curveSubdivision.cyclic(UnitVector.of(6, 3));
    assertEquals(cyclic, Tensors.fromString("{0, 0, -5/128, -7/128, 35/128, 105/128, 105/128, 35/128, -7/128, -5/128, 0, 0}"));
  }
}
