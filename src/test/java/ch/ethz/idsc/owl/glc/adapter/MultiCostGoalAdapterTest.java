// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2ShiftCostFunction;
import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class MultiCostGoalAdapterTest extends TestCase {
  public void testSimple() {
    Scalar speed = RealScalar.of(2);
    FlowsInterface carFlows = Se2CarFlows.forward(speed, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(9);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(10, 5, 1), Tensors.vector(1, 1, 2));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    CostFunction costFunction = new Se2ShiftCostFunction(RealScalar.of(3));
    GoalInterface mcga = MultiCostGoalAdapter.of(goalInterface, Arrays.asList(costFunction));
    {
      Optional<StateTime> optional = mcga.firstMember(Arrays.asList(new StateTime(Tensors.vector(10, 5, 0), RealScalar.ZERO)));
      assertTrue(optional.isPresent());
    }
    {
      Optional<StateTime> optional = mcga.firstMember(Arrays.asList(new StateTime(Tensors.vector(10, 5, 4), RealScalar.ZERO)));
      assertFalse(optional.isPresent());
    }
    Scalar minCostToGoal = mcga.minCostToGoal(Tensors.vector(0, 5, 0));
    assertEquals(minCostToGoal, RealScalar.of(9).divide(speed));
  }

  public void testMembers() {
    Scalar speed = RealScalar.of(2);
    FlowsInterface carFlows = Se2CarFlows.forward(speed, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(9);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(10, 5, 1), Tensors.vector(1, 1, 2));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    CostFunction costFunction = new Se2ShiftCostFunction(RealScalar.of(3));
    GoalInterface mcga = MultiCostGoalAdapter.of(goalInterface, Arrays.asList(costFunction));
    assertTrue(mcga.isMember(new StateTime(Tensors.vector(10, 5, 1), RealScalar.ZERO)));
    assertFalse(mcga.isMember(new StateTime(Tensors.vector(10, 5, 3.1), RealScalar.ZERO)));
  }

  public void testTrivial() {
    Scalar speed = RealScalar.of(2);
    FlowsInterface carFlows = Se2CarFlows.forward(speed, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(9);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(10, 5, 1), Tensors.vector(1, 1, 2));
    Se2MinTimeGoalManager goalInterface2 = //
        new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = goalInterface2.getGoalInterface();
    GoalInterface mcga = MultiCostGoalAdapter.of(goalInterface, Arrays.asList());
    assertTrue(mcga == goalInterface);
  }
}
