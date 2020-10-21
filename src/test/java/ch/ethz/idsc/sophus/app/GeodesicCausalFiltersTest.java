// code by jph
package ch.ethz.idsc.sophus.app;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import junit.framework.TestCase;

public class GeodesicCausalFiltersTest extends TestCase {
  public void testSimple() {
    for (GeodesicDisplay geodesicDisplay : GeodesicDisplays.LIE_GROUPS)
      for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
        for (int radius = 0; radius < 3; ++radius)
          for (GeodesicCausalFilters geodesicCausalFilters : GeodesicCausalFilters.values()) {
            TensorUnaryOperator tensorUnaryOperator = geodesicCausalFilters.supply(geodesicDisplay, smoothingKernel, radius, RationalScalar.HALF);
            assertNotNull(tensorUnaryOperator);
          }
  }
}
