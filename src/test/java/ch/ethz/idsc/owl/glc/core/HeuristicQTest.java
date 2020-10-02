// code by jph
package ch.ethz.idsc.owl.glc.core;

import ch.ethz.idsc.owl.bot.se2.ScaledLateralAcceleration;
import ch.ethz.idsc.owl.bot.se2.Se2LateralAcceleration;
import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class HeuristicQTest extends TestCase {
  public void testSimple() {
    assertFalse(HeuristicQ.of(Se2LateralAcceleration.INSTANCE));
    assertFalse(HeuristicQ.of(new ScaledLateralAcceleration(RealScalar.ONE)));
  }

  public void testFail() {
    AssertFail.of(() -> HeuristicQ.of(null));
  }
}
