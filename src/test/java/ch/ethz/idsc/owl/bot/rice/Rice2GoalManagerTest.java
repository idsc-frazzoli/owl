// code by jph
package ch.ethz.idsc.owl.bot.rice;

import junit.framework.TestCase;

public class Rice2GoalManagerTest extends TestCase {
  public void testSimple() {
    try {
      new Rice2GoalManager(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
