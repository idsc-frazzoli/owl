// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class StaticRatioLimitTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    StaticRatioLimit staticRatioLimit = Serialization.copy(new StaticRatioLimit(Quantity.of(2, "m^-1")));
    Clip clip = staticRatioLimit.at(null, null);
    clip.requireInside(Quantity.of(-2, "m^-1"));
    clip.requireInside(Quantity.of(+2, "m^-1"));
    assertFalse(clip.isInside(Quantity.of(-3, "m^-1")));
    assertFalse(clip.isInside(Quantity.of(+3, "m^-1")));
  }
}
