// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gds.Se2ClothoidDisplay;
import ch.ethz.idsc.sophus.gds.Se2Display;
import junit.framework.TestCase;

public class TransitionNdTypesTest extends TestCase {
  public void testSimple() {
    assertEquals(TransitionNdTypes.fromString(Se2ClothoidDisplay.ANALYTIC), TransitionNdTypes.CLOTHOID_ANALYTIC);
    assertEquals(TransitionNdTypes.fromString(Se2ClothoidDisplay.LEGENDRE), TransitionNdTypes.CLOTHOID_LEGENDRE);
    assertEquals(TransitionNdTypes.fromString(Se2Display.INSTANCE), TransitionNdTypes.DUBINS);
    assertEquals(TransitionNdTypes.fromString(R2Display.INSTANCE), TransitionNdTypes.RN);
  }
}
