// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ControlMidpointsTest extends TestCase {
  public void testSimple() {
    CurveSubdivision curveSubdivision = ControlMidpoints.of(RnGeodesic.INSTANCE);
    Tensor tensor = curveSubdivision.string(Tensors.vector(1, 2, 3));
    assertEquals(tensor.toString(), "{1, 3/2, 5/2, 3}");
  }
}
