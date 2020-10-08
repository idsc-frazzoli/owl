// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
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

  public void testRemoveChildren() {
    StandardRelaxedLexicographicPlanner relaxedTrajectoryPlanner = (StandardRelaxedLexicographicPlanner) TestHelper.createPlanner();
    Tensor state = Tensors.vector(10, 10);
    Tensor state2 = Tensors.vector(30, 30);
    GlcNode node0 = GlcNode.of(null, new StateTime(state, RealScalar.ZERO), VectorScalar.of(2, 1), VectorScalar.of(0, 0));
    GlcNode node1 = GlcNode.of(null, new StateTime(state, RealScalar.ZERO), VectorScalar.of(1, 2), VectorScalar.of(0, 0));
    GlcNode node2 = GlcNode.of(null, new StateTime(state2, RealScalar.ZERO), VectorScalar.of(1, 1), VectorScalar.of(0, 0));
    relaxedTrajectoryPlanner.addToGlobalQueue(node0);
    relaxedTrajectoryPlanner.addToGlobalQueue(node1);
    relaxedTrajectoryPlanner.addToGlobalQueue(node2);
    node0.insertEdgeTo(node1);
    node1.insertEdgeTo(node2);
    final Tensor domainKey0 = relaxedTrajectoryPlanner.stateTimeRaster.convertToKey(node0.stateTime());
    final Tensor domainKey1 = relaxedTrajectoryPlanner.stateTimeRaster.convertToKey(node1.stateTime());
    final Tensor domainKey2 = relaxedTrajectoryPlanner.stateTimeRaster.convertToKey(node2.stateTime());
    relaxedTrajectoryPlanner.addToDomainMap(domainKey0, node0);
    relaxedTrajectoryPlanner.addToDomainMap(domainKey1, node1);
    relaxedTrajectoryPlanner.addToDomainMap(domainKey2, node2);
    relaxedTrajectoryPlanner.removeChildren(Arrays.asList(node1));
    assertFalse(relaxedTrajectoryPlanner.getQueue().contains(node1));
    assertFalse(relaxedTrajectoryPlanner.getQueue().contains(node2));
  }
}
