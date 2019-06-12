// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GeodesicDeBoorTest extends TestCase {
  public void testSimple() {
    Tensor knots = Tensors.vector(1, 2, 3, 4);
    Tensor control = Tensors.vector(9, 3, 4);
    GeodesicDeBoor.of(RnGeodesic.INSTANCE, knots, control);
    try {
      GeodesicDeBoor.of(null, knots, control);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
