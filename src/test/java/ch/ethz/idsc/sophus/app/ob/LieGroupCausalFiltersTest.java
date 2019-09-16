// code by jph
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class LieGroupCausalFiltersTest extends TestCase {
  public void testSimple() {
    for (GeodesicDisplay geodesicDisplay : GeodesicDisplays.LIE_GROUPS)
      for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
        for (int radius = 0; radius < 3; ++radius)
          for (LieGroupCausalFilters lieGroupCausalFilters : LieGroupCausalFilters.values()) {
            TensorUnaryOperator tensorUnaryOperator = lieGroupCausalFilters.supply(geodesicDisplay, smoothingKernel, radius, RationalScalar.HALF);
            assertNotNull(tensorUnaryOperator);
          }
  }
}
