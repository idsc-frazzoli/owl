// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.util.DemoInterfaceHelper;
import junit.framework.TestCase;

public class RLTrajectoryPlanner0DemoTest extends TestCase {
  public void testSimple() {
    DemoInterfaceHelper.brief(new RLTrajectoryPlanner0Demo());
  }

  public void testPresent() {
    assertTrue(RLTrajectoryPlanner0Demo.getBest().isPresent());
  }
}
