// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidCurvatureQueryTest extends TestCase {
  public void testWidthZeroFail() {
    AssertFail.of(() -> new ClothoidCurvatureQuery(Clips.interval(3, 3)));
  }

  public void testNullFail() {
    AssertFail.of(() -> new ClothoidCurvatureQuery((Clip) null));
  }
}
