package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import junit.framework.TestCase;

public class ApTrajectoryPlannerTest extends TestCase {
  public void testIsStandardTrajectoryPlanner() {
    StandardTrajectoryPlanner apTrajectoryPlanner = ApTrajectoryPlanner.ApStandardTrajectoryPlanner();
    assertTrue(apTrajectoryPlanner instanceof StandardTrajectoryPlanner);
  }
}
