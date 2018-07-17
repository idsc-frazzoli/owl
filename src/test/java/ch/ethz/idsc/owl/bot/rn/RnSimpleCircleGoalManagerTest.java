// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnSimpleCircleGoalManagerTest extends TestCase {
  public void testMinCostToGoal1() {
    GoalInterface rnGoal = RnMinDistGoalManager.sperical(Tensors.vector(5, 0), RealScalar.of(2));
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

  public void testNoHeuristic1() {
    RnNoHeuristicCircleGoalManager rnGoal = new RnNoHeuristicCircleGoalManager(Tensors.vector(5, 0), RealScalar.of(2));
    assertFalse(HeuristicQ.of(rnGoal));
  }
}
