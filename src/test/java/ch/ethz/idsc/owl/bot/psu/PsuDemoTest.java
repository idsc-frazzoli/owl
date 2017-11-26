// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import junit.framework.TestCase;

public class PsuDemoTest extends TestCase {
  public void testFindGoal() {
    TrajectoryPlanner trajectoryPlanner = PsuDemo.simple();
    assertTrue(trajectoryPlanner.getBest().isPresent());
  }
}
