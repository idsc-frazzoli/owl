// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.data.tree.Node;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.CheckedTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicConsistency;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import junit.framework.TestCase;

public class PsuDemoTest extends TestCase {
  public void testFindGoal() {
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI * 0.7, 0.5), RealScalar.of(0.3));
    TrajectoryPlanner trajectoryPlanner = CheckedTrajectoryPlanner.wrap(PsuDemo.raw(goalInterface));
    assertFalse(trajectoryPlanner.getBest().isPresent());
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    assertFalse(trajectoryPlanner.getBest().isPresent());
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    // System.out.println("expan=" + glcExpand.getExpandCount());
    assertTrue(trajectoryPlanner.getBest().isPresent());
    // TODO JPH the code below is a general consistency check:
    Collection<GlcNode> collection = trajectoryPlanner.getDomainMap().values();
    // System.out.println("nodes=" + collection.size());
    assertTrue(100 < collection.size());
    long count = collection.stream().map(Node::parent).filter(Objects::isNull).count();
    assertEquals(count, 1);
    HeuristicConsistency.check(trajectoryPlanner);
  }
}
