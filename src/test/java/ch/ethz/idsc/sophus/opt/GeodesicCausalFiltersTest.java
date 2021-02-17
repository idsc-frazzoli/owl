// code by jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class GeodesicCausalFiltersTest extends TestCase {
  public void testSimple() {
    for (ManifoldDisplay geodesicDisplay : GeodesicDisplays.LIE_GROUPS)
      for (WindowFunctions smoothingKernel : WindowFunctions.values())
        for (int radius = 0; radius < 3; ++radius)
          for (GeodesicCausalFilters geodesicCausalFilters : GeodesicCausalFilters.values()) {
            TensorUnaryOperator tensorUnaryOperator = geodesicCausalFilters.supply(geodesicDisplay, smoothingKernel.get(), radius, RationalScalar.HALF);
            assertNotNull(tensorUnaryOperator);
          }
  }
}
