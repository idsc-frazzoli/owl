// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Random;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import junit.framework.TestCase;

public class RelaxedDomainQueueTest extends TestCase {
  public void testAdd() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(2, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(2, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    RelaxedDomainQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    assertTrue(rlQueue.openSet.contains(node1));
    rlQueue.add(node2);
    assertTrue(rlQueue.openSet.contains(node1) && rlQueue.openSet.contains(node2));
    rlQueue.add(node3);
    assertTrue(rlQueue.openSet.contains(node1) && rlQueue.openSet.contains(node2) && rlQueue.openSet.contains(node3));
    rlQueue.add(node4);
    assertTrue(rlQueue.openSet.contains(node1) && rlQueue.openSet.contains(node2) && rlQueue.openSet.contains(node3));
    assertFalse(rlQueue.openSet.contains(node4));
    rlQueue.add(node5);
    assertTrue(rlQueue.openSet.contains(node5) && rlQueue.openSet.contains(node2));
    assertFalse(rlQueue.openSet.contains(node1) && rlQueue.openSet.contains(node3) && rlQueue.openSet.contains(node4));
  }

  public void testPeek() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(2, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(2, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    RelaxedDomainQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    assertTrue(rlQueue.openSet.contains(node1));
    assertTrue(rlQueue.peek() == node1);
    assertTrue(rlQueue.openSet.size() == 1);
    rlQueue.add(node2);
    assertTrue(rlQueue.peek() == node2);
    assertTrue(rlQueue.openSet.size() == 2);
    rlQueue.add(node3);
    assertTrue(rlQueue.peek() == node2);
    assertTrue(rlQueue.openSet.size() == 3);
    rlQueue.add(node4);
    assertTrue(rlQueue.peek() == node2);
    assertTrue(rlQueue.openSet.size() == 3);
    rlQueue.add(node5);
    assertTrue(rlQueue.peek() == node5);
    assertTrue(rlQueue.openSet.size() == 2);
  }

  public void testPoll() {
    Tensor slacks = Tensors.vector(3, 3, 3);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(2, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    RelaxedDomainQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    rlQueue.add(node2);
    rlQueue.add(node3);
    rlQueue.add(node4);
    rlQueue.add(node5);
    assertTrue(rlQueue.openSet.size() == 5);
    assertTrue(rlQueue.poll() == node5);
    assertTrue(rlQueue.openSet.size() == 4);
    assertTrue(rlQueue.poll() == node1);
    assertTrue(rlQueue.openSet.size() == 3);
    assertTrue(rlQueue.poll() == node2);
    assertTrue(rlQueue.openSet.size() == 2);
    assertTrue(rlQueue.poll() == node3);
    assertTrue(rlQueue.openSet.size() == 1);
    assertTrue(rlQueue.poll() == node4);
    assertTrue(rlQueue.openSet.isEmpty());
  }

  public void testSpeed() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    Random random = new Random();
    Scalar costFromRoot = VectorScalar.of(Tensors.vectorDouble(random.doubles(3, 1, 2).toArray()));
    Scalar minCostToGoal = VectorScalar.of(0, 0, 0);
    GlcNode firstNode = GlcNode.of(null, null, costFromRoot, minCostToGoal);
    RelaxedDomainQueue rlQueue = RelaxedDomainQueue.singleton(firstNode, slacks);
    for (int i = 0; i < 1000; ++i) {
      costFromRoot = VectorScalar.of(Tensors.vectorDouble(random.doubles(3, 1, 2).toArray()));
      minCostToGoal = VectorScalar.of(0, 0, 0);
      GlcNode node = GlcNode.of(null, null, costFromRoot, minCostToGoal);
      rlQueue.add(node);
    }
    Timing timing = Timing.started();
    rlQueue.poll();
    System.out.println(timing.seconds());
  }
}
