// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.Collections;

import ch.ethz.idsc.owl.glc.adapter.GlcNodes;
import ch.ethz.idsc.owl.glc.adapter.HeuristicQ;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnSimpleCircleGoalManagerTest extends TestCase {
  public void testMinCostToGoal1() {
    GoalInterface rnGoal = RnMinDistSphericalGoalManager.create(Tensors.vector(5, 0), RealScalar.of(2));
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(2, 0)), RealScalar.ONE);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(3, 0)), RealScalar.ZERO);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(4, 0)), RealScalar.ZERO);
  }

  public void testMinCostToGoal2() {
    RnNoHeuristicCircleGoalManager rnGoal = new RnNoHeuristicCircleGoalManager(Tensors.vector(5, 0), RealScalar.of(2));
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(2, 0)), RealScalar.ZERO);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(3, 0)), RealScalar.ZERO);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(4, 0)), RealScalar.ZERO);
  }

  public void testCostIncrement1() {
    GlcNode root = GlcNodes.createRoot(new StateTime(Tensors.vector(2, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    GoalInterface rnGoal = RnMinDistSphericalGoalManager.create(Tensors.vector(5, 0), RealScalar.of(2));
    Scalar incr = rnGoal.costIncrement( //
        root, Collections.singletonList(new StateTime(Tensors.vector(10, 2), RealScalar.ZERO)), null);
    assertEquals(incr, RealScalar.of(8));
  }

  public void testCostIncrement2() {
    GlcNode root = GlcNodes.createRoot(new StateTime(Tensors.vector(2, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    RnNoHeuristicCircleGoalManager rnGoal = new RnNoHeuristicCircleGoalManager(Tensors.vector(5, 0), RealScalar.of(2));
    Scalar incr = rnGoal.costIncrement( //
        root, Collections.singletonList(new StateTime(Tensors.vector(10, 2), RealScalar.ZERO)), null);
    assertEquals(incr, RealScalar.of(8));
  }

  public void testNoHeuristic1() {
    RnNoHeuristicCircleGoalManager rnGoal = new RnNoHeuristicCircleGoalManager(Tensors.vector(5, 0), RealScalar.of(2));
    assertFalse(HeuristicQ.of(rnGoal));
  }
}
