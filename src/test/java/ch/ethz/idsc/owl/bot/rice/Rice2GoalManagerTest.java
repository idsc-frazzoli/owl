// code by jph
package ch.ethz.idsc.owl.bot.rice;

import ch.ethz.idsc.owl.math.AssertFail;
import junit.framework.TestCase;

public class Rice2GoalManagerTest extends TestCase {
  public void testSimple() {
    AssertFail.of(() -> 
      new Rice2GoalManager(null));
  }
}
