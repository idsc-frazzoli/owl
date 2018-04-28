// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.glc.CarFlows;
import ch.ethz.idsc.owl.bot.se2.glc.CarStandardFlows;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2MinTimeGoalManagerTest extends TestCase {
  public void testSimple() {
    CarFlows carFlows = new CarStandardFlows(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(3);
    new Se2MinTimeGoalManager(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 3), controls);
    try {
      new Se2MinTimeGoalManager(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 3), controls);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testIsMember() {
    CarFlows carFlows = new CarStandardFlows(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(3);
    Se2AbstractGoalManager se2AbstractGoalManager = //
        new Se2MinTimeGoalManager(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1), controls);
    assertEquals(se2AbstractGoalManager.radiusSpace(), RealScalar.ONE);
    assertTrue(se2AbstractGoalManager.isMember(Tensors.vector(1, 2, 3)));
    assertFalse(se2AbstractGoalManager.isMember(Tensors.vector(-1, 2, 3)));
    assertFalse(se2AbstractGoalManager.isMember(Tensors.vector(1, 2, 3.2)));
  }

  public void testGoalAdapter() {
    CarFlows carFlows = new CarStandardFlows(RealScalar.ONE, RealScalar.ONE);
    Collection<Flow> controls = carFlows.getFlows(3);
    Se2AbstractGoalManager se2AbstractGoalManager = //
        new Se2MinTimeGoalManager(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1), controls);
    GoalInterface goalInterface = se2AbstractGoalManager.getGoalInterface();
    assertTrue(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(-1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.isMember(new StateTime(Tensors.vector(1, 2, 3.2), RealScalar.ZERO)));
  }
}
