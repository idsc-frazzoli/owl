// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidCurvatureQueryTest extends TestCase {
  public void testWidthZeroFail() {
    try {
      new ClothoidCurvatureQuery(Clips.interval(3, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullFail() {
    try {
      new ClothoidCurvatureQuery((Clip) null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
