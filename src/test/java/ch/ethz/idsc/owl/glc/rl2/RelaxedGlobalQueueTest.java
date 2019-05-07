// code astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RelaxedGlobalQueueTest extends TestCase {
  public void testSimple() {
    Tensor slacks = Tensors.vector(3, 3, 3);
    RelaxedGlobalQueue rlQueue = new RelaxedGlobalQueue(slacks);
    assertTrue(rlQueue.openSet.isEmpty());
  }

  public void testAdd() {
    Tensor slacks = Tensors.vector(3, 3, 3);
    RelaxedGlobalQueue rlQueue = new RelaxedGlobalQueue(slacks);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(2, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    rlQueue.add(node1);
    assertTrue(rlQueue.size() == 1);
    assertTrue(rlQueue.openSet.contains(node1));
    rlQueue.add(node2);
    assertTrue(rlQueue.size() == 2);
    assertTrue(rlQueue.openSet.contains(node2));
    rlQueue.add(node3);
    assertTrue(rlQueue.size() == 3);
    assertTrue(rlQueue.openSet.contains(node3));
    rlQueue.add(node4);
    assertTrue(rlQueue.size() == 4);
    assertTrue(rlQueue.openSet.contains(node4));
    rlQueue.add(node5);
    assertTrue(rlQueue.size() == 5);
    assertTrue(rlQueue.openSet.contains(node5));
  }

  public void testPeek() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    RelaxedGlobalQueue rlQueue = new RelaxedGlobalQueue(slacks);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(1, 1, 1), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    rlQueue.add(node1);
    assertTrue(rlQueue.peek() == node1);
    rlQueue.add(node2);
    assertTrue(rlQueue.peek() == node1);
    rlQueue.add(node3);
    assertTrue(rlQueue.peek() == node3);
    rlQueue.add(node4);
    assertTrue(rlQueue.peek() == node3);
    rlQueue.add(node5);
    assertTrue(rlQueue.peek() == node5);
    assertTrue(rlQueue.size() == 5);
  }

  public void testPoll() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    RelaxedGlobalQueue rlQueue = new RelaxedGlobalQueue(slacks);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(1, 1, 1), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    rlQueue.add(node1);
    rlQueue.add(node2);
    rlQueue.add(node3);
    rlQueue.add(node4);
    rlQueue.add(node5);
    assertTrue(rlQueue.size() == 5);
    assertTrue(rlQueue.poll() == node5);
    assertTrue(rlQueue.size() == 4);
    assertTrue(rlQueue.poll() == node3);
    assertTrue(rlQueue.size() == 3);
    assertTrue(rlQueue.poll() == node1);
    assertTrue(rlQueue.size() == 2);
    assertTrue(rlQueue.poll() == node2);
    assertTrue(rlQueue.size() == 1);
    assertTrue(rlQueue.poll() == node4);
    assertTrue(rlQueue.openSet.isEmpty());
  }

  public void testRemoveAll() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    RelaxedGlobalQueue rlQueue = new RelaxedGlobalQueue(slacks);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(1, 1, 1), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    rlQueue.add(node1);
    rlQueue.add(node2);
    rlQueue.add(node3);
    rlQueue.add(node4);
    rlQueue.add(node5);
    List<GlcNode> removeList = new LinkedList<>();
    removeList.add(node1);
    assertTrue(rlQueue.size() == 5);
    rlQueue.removeAll(removeList);
    removeList.clear();
    assertTrue(rlQueue.size() == 4);
    assertFalse(rlQueue.openSet.contains(node1));
    removeList.add(node2);
    removeList.add(node3);
    removeList.add(node4);
    removeList.add(node5);
    rlQueue.removeAll(removeList);
    assertTrue(rlQueue.openSet.isEmpty());
  }
}
