// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.io.IOException;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.VectorScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class StandardRelaxedLexicographicPlannerTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = Serialization.copy(TestHelper.createPlanner());
    Objects.requireNonNull(relaxedTrajectoryPlanner.getStateIntegrator());
    assertTrue(relaxedTrajectoryPlanner.getQueue().isEmpty());
    Objects.requireNonNull(relaxedTrajectoryPlanner.getBest());
    assertTrue(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().isEmpty());
    Tensor stateRoot = Tensors.vector(0, 0);
    relaxedTrajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    assertFalse(relaxedTrajectoryPlanner.getQueue().isEmpty());
    assertEquals(RelaxedDebugUtils.allNodes(relaxedTrajectoryPlanner).size(), 1);
    relaxedTrajectoryPlanner.pollNext();
    assertTrue(relaxedTrajectoryPlanner.getQueue().isEmpty());
    assertFalse(RelaxedDebugUtils.allNodes(relaxedTrajectoryPlanner).isEmpty());
  }

  public void testAddToGlobal() {
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = TestHelper.createPlanner();
    Tensor state = Tensors.vector(10, 10);
    GlcNode node1 = GlcNode.of(null, new StateTime(state, RealScalar.ZERO), VectorScalar.of(1, 2), VectorScalar.of(0, 0));
    GlcNode node2 = GlcNode.of(null, new StateTime(state, RealScalar.ZERO), VectorScalar.of(2, 1), VectorScalar.of(0, 0));
    relaxedTrajectoryPlanner.addToGlobalQueue(node1);
    relaxedTrajectoryPlanner.addToGlobalQueue(node2);
    assertTrue(relaxedTrajectoryPlanner.getQueue().contains(node1));
    assertTrue(relaxedTrajectoryPlanner.getQueue().contains(node2));
  }
}
