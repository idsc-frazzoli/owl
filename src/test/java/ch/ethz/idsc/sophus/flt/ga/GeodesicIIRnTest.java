// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class GeodesicIIRnTest extends TestCase {
  public void testFailOpNull() {
    try {
      GeodesicIIRn.of(null, RnGeodesic.INSTANCE, 3, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailGeodesicNull() {
    try {
      GeodesicIIRn.of(x -> x.get(0), null, 3, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
