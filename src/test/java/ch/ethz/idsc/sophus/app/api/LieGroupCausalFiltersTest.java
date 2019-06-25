// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class LieGroupCausalFiltersTest extends TestCase {
  public void testSimple() {
    for (GeodesicDisplay geodesicDisplay : GeodesicDisplays.ALL) {
      LieGroup lieGroup = geodesicDisplay.lieGroup();
      if (Objects.nonNull(lieGroup))
        for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
          for (int radius = 0; radius < 3; ++radius)
            for (LieGroupCausalFilters lieGroupCausalFilters : LieGroupCausalFilters.values()) {
              TensorUnaryOperator tensorUnaryOperator = lieGroupCausalFilters.supply(geodesicDisplay, smoothingKernel, radius, RationalScalar.HALF);
              assertNotNull(tensorUnaryOperator);
            }
    }
  }
}
