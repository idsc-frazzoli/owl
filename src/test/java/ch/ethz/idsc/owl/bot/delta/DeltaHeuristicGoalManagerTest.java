// code by jl
package ch.ethz.idsc.owl.bot.delta;

import ch.ethz.idsc.owl.glc.adapter.HeuristicQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DeltaHeuristicGoalManagerTest extends TestCase {
  public void testSimple() {
    DeltaHeuristicGoalManager deltaGoal = new DeltaHeuristicGoalManager(//
        Tensors.vector(0, 0), Tensors.vector(1, 1), RealScalar.ONE);
    assertTrue(HeuristicQ.of(deltaGoal));
    assertEquals(deltaGoal.minCostToGoal(Tensors.vector(2, 0)), RealScalar.ONE);
    assertEquals(deltaGoal.minCostToGoal(Tensors.vector(1, 0)), RealScalar.ZERO);
    assertEquals(deltaGoal.minCostToGoal(Tensors.vector(0, 0)), RealScalar.ZERO);
  }
}
