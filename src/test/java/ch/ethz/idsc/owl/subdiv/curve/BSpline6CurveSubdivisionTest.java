// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
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
    assertTrue(ExactScalarQ.all(tensor));
  }
}
