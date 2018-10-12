// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BSpline5CurveSubdivisionTest extends TestCase {
  public void testCyclicMask() {
    BSpline5CurveSubdivision curveSubdivision = new BSpline5CurveSubdivision(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.cyclic(Tensors.vector(1, 0, 0, 0, 0, 0, 0));
    assertEquals(tensor, Tensors.fromString( //
        "{5/8, 15/32, 3/16, 1/32, 0, 0, 0, 0, 0, 0, 0, 1/32, 3/16, 15/32}"));
  }
}
