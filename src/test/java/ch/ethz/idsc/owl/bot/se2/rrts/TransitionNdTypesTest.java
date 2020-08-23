// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2ClothoidDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import junit.framework.TestCase;

public class TransitionNdTypesTest extends TestCase {
  public void testSimple() {
    assertEquals(TransitionNdTypes.fromString(Se2ClothoidDisplay.ANALYTIC), TransitionNdTypes.CLOTHOID_ANALYTIC);
    assertEquals(TransitionNdTypes.fromString(Se2ClothoidDisplay.LEGENDRE), TransitionNdTypes.CLOTHOID_LEGENDRE);
    assertEquals(TransitionNdTypes.fromString(Se2GeodesicDisplay.INSTANCE), TransitionNdTypes.DUBINS);
    assertEquals(TransitionNdTypes.fromString(R2GeodesicDisplay.INSTANCE), TransitionNdTypes.RN);
  }
}
