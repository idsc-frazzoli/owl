// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.io.IOException;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.glc.CarFlows;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class Se2MinTimeGoalManagerTest extends TestCase {
  public void testIsMember() {
    FlowsInterface carFlows = CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(3))));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(-1, 2, 3), RealScalar.of(3))));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3.2), RealScalar.of(3))));
  }

  public void testGoalAdapter() {
    FlowsInterface carFlows = CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(-1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3.2), RealScalar.ZERO)));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    FlowsInterface carFlows = CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Serialization.copy(carFlows);
    Collection<Flow> controls = carFlows.getFlows(3);
    Serialization.copy(controls);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.spherical(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Serialization.copy(se2ComboRegion);
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    Serialization.copy(se2MinTimeGoalManager);
  }
}
