// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class TransitionCurvatureQueryTest extends TestCase {
  public void testSimple() {
    try {
      new ClothoidCurvatureQuery((Clip) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
