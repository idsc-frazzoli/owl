// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2ClothoidDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import junit.framework.TestCase;

public class Se2TransitionNdTypeTest extends TestCase {
  public void testSimple() {
    assertEquals(Se2TransitionNdType.fromString(Se2ClothoidDisplay.INSTANCE.toString()), Se2TransitionNdType.CLOTHOID);
    assertEquals(Se2TransitionNdType.fromString(Se2GeodesicDisplay.INSTANCE.toString()), Se2TransitionNdType.DUBINS);
    assertEquals(Se2TransitionNdType.fromString(R2GeodesicDisplay.INSTANCE.toString()), Se2TransitionNdType.R2);
  }
}
