// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class ClothoidFixedControlTest extends TestCase {
  public void testNullFail() {
    try {
      new ClothoidFixedControl(null, RealScalar.of(2));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new ClothoidFixedControl(RealScalar.of(2), null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
