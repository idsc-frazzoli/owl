// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.io.IOException;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2MinTimeGoalManagerTest extends TestCase {
  public void testIsMember() {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(3))));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(-1, 2, 3), RealScalar.of(3))));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3.2), RealScalar.of(3))));
  }

  public void testGoalAdapter() {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(-1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3.2), RealScalar.ZERO)));
  }

  public void testQuantity() {
    FlowsInterface carFlows = Se2CarFlows.standard(Quantity.of(1, "m*s^-1"), Quantity.of(.5, "m^-1"));
    Collection<Flow> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical( //
        Tensors.fromString("{1[m], 2[m], 3}"), //
        Tensors.fromString("{1[m], 1[m], 0.1}"));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.isMember(new StateTime(Tensors.fromString("{1[m], 2[m], 3}"), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.fromString("{-1[m], 2[m], 3}"), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.fromString("{1[m], 2[m], 3.2}"), RealScalar.ZERO)));
    {
      Scalar minCostToGoal = goalInterface.minCostToGoal(Tensors.fromString("{1[m], 2[m], 3.2}"));
      assertTrue(Chop._10.close(Quantity.of(0.2, "s"), minCostToGoal));
    }
    {
      Scalar minCostToGoal = goalInterface.minCostToGoal(Tensors.fromString("{15[m], 22[m], 3.1}"));
      assertTrue(Chop._10.close(Quantity.of(23.413111231467404, "s"), minCostToGoal));
    }
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Serialization.copy(carFlows);
    Collection<Flow> controls = carFlows.getFlows(3);
    Serialization.copy(controls);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Serialization.copy(se2ComboRegion);
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    Serialization.copy(se2MinTimeGoalManager);
  }
}
