// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.io.IOException;
import java.util.Collections;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.HeuristicQ;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RnMinDistGoalManagerTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    GoalInterface goalInterface = Serialization.copy(RnMinDistGoalManager.sperical(Tensors.vector(10, 10, 10), RealScalar.of(2)));
    Scalar scalar = goalInterface.minCostToGoal(Tensors.vector(10, 0, 10));
    assertEquals(scalar, RealScalar.of(8));
  }

  public void testQuantity() {
    GoalInterface goalInterface = RnMinDistGoalManager.sperical(Tensors.fromString("{10[m], 10[m], 10[m]}"), Quantity.of(2, "m"));
    Scalar scalar = goalInterface.minCostToGoal(Tensors.fromString("{10[m], 0[m], 10[m]}"));
    assertEquals(scalar, Quantity.of(8, "m"));
  }

  public void testHeuristic() {
    GoalInterface goalInterface = //
        RnMinDistGoalManager.sperical(Tensors.vector(5, 0), RealScalar.of(2));
    assertTrue(HeuristicQ.of(goalInterface));
  }

  public void testHeuristic2() {
    BallRegion ballRegion = new BallRegion(Tensors.vector(5, 0), RealScalar.of(2));
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    assertTrue(HeuristicQ.of(goalInterface));
  }

  public void testMinCost() {
    GoalInterface goalInterface = //
        RnMinDistGoalManager.sperical(Tensors.vector(5, 3), RealScalar.of(2));
    assertEquals(goalInterface.minCostToGoal(Tensors.vector(0, 3)), RealScalar.of(3));
    assertEquals(goalInterface.minCostToGoal(Tensors.vector(5, 1)), RealScalar.of(0));
    assertEquals(goalInterface.minCostToGoal(Tensors.vector(5, 0)), RealScalar.of(1));
  }

  public void testMinCost2() {
    BallRegion ballRegion = new BallRegion(Tensors.vector(5, 3), RealScalar.of(2));
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    assertEquals(goalInterface.minCostToGoal(Tensors.vector(0, 3)), RealScalar.of(3));
    assertEquals(goalInterface.minCostToGoal(Tensors.vector(5, 1)), RealScalar.of(0));
    assertEquals(goalInterface.minCostToGoal(Tensors.vector(5, 0)), RealScalar.of(1));
  }

  public void testCostIncr() {
    GlcNode glcNode = GlcNode.of(null, new StateTime(Tensors.vector(10, 3), RealScalar.ZERO), RealScalar.ZERO, RealScalar.ZERO);
    BallRegion ballRegion = new BallRegion(Tensors.vector(5, 3), RealScalar.of(2));
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    Scalar increment = goalInterface.costIncrement( //
        glcNode, //
        Collections.singletonList(new StateTime(Tensors.vector(13, 7), RealScalar.ZERO)), //
        null);
    assertEquals(increment, RealScalar.of(5));
  }
}
