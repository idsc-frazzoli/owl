// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class ClothoidFixedControlTest extends TestCase {
  public void testNullFail() {
    AssertFail.of(() -> new ClothoidFixedControl(null, RealScalar.of(2)));
    AssertFail.of(() -> new ClothoidFixedControl(RealScalar.of(2), null));
  }
}
