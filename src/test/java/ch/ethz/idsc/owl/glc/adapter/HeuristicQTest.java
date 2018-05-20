// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.owl.bot.se2.Se2LateralAcceleration;
import junit.framework.TestCase;

public class HeuristicQTest extends TestCase {
  public void testSimple() {
    assertFalse(HeuristicQ.of(Se2LateralAcceleration.INSTANCE));
  }

  public void testFail() {
    try {
      HeuristicQ.of(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
