// code by jph
package ch.ethz.idsc.owl.bot.rice;

import ch.ethz.idsc.owl.glc.core.HeuristicAssert;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import junit.framework.TestCase;

public class Rice1dDemoTest extends TestCase {
  public void testFindGoal() {
    GlcTrajectoryPlanner trajectoryPlanner = Rice1dDemo.simple();
    assertTrue(trajectoryPlanner.getBest().isPresent());
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
  }
}
