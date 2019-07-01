// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.util.Collection;

import ch.ethz.idsc.owl.data.tree.NodesAssert;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.CheckedGlcTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicAssert;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import junit.framework.TestCase;

public class PsuDemoTest extends TestCase {
  public void testFindGoal() {
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI * 0.7, 0.5), RealScalar.of(0.3));
    GlcTrajectoryPlanner trajectoryPlanner = CheckedGlcTrajectoryPlanner.wrap(PsuDemo.raw(goalInterface));
    HeuristicAssert.check(trajectoryPlanner);
    assertFalse(trajectoryPlanner.getBest().isPresent());
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    assertFalse(trajectoryPlanner.getBest().isPresent());
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    HeuristicAssert.check(trajectoryPlanner);
    assertTrue(trajectoryPlanner.getBest().isPresent());
    Collection<GlcNode> collection = trajectoryPlanner.getDomainMap().values();
    assertTrue(100 < collection.size());
    NodesAssert.containsOneRoot(collection);
  }
}
