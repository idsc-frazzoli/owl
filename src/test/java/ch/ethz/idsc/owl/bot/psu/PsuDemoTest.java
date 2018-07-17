// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.data.tree.Node;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.DebugUtils;
import junit.framework.TestCase;

public class PsuDemoTest extends TestCase {
  public void testFindGoal() {
    TrajectoryPlanner trajectoryPlanner = PsuDemo.simple();
    assertTrue(trajectoryPlanner.getBest().isPresent());
    Collection<GlcNode> values = trajectoryPlanner.getDomainMap().values();
    assertTrue(100 < values.size());
    long count = values.stream().map(Node::parent).filter(Objects::isNull).count();
    assertEquals(count, 1);
    DebugUtils.heuristicConsistencyCheck(trajectoryPlanner);
    DebugUtils.nodeAmountCompare(trajectoryPlanner);
  }
}
