// code by jph
package ch.ethz.idsc.owl.bot.rice;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import junit.framework.TestCase;

public class Rice1dDemoTest extends TestCase {
  public void testFindGoal() {
    TrajectoryPlanner trajectoryPlanner = Rice1dDemo.simple();
    assertTrue(trajectoryPlanner.getBest().isPresent());
  }
}
