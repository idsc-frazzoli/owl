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
    assertTrue(rlQueue.collection().isEmpty());
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
    assertTrue(rlQueue.collection().size() == 1);
    assertTrue(rlQueue.collection().contains(node1));
    rlQueue.add(node2);
    assertTrue(rlQueue.collection().size() == 2);
    assertTrue(rlQueue.collection().contains(node2));
    rlQueue.add(node3);
    assertTrue(rlQueue.collection().size() == 3);
    assertTrue(rlQueue.collection().contains(node3));
    rlQueue.add(node4);
    assertTrue(rlQueue.collection().size() == 4);
    assertTrue(rlQueue.collection().contains(node4));
    rlQueue.add(node5);
    assertTrue(rlQueue.collection().size() == 5);
    assertTrue(rlQueue.collection().contains(node5));
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
    assertTrue(rlQueue.peekBest() == node1);
    rlQueue.add(node2);
    assertTrue(rlQueue.peekBest() == node1);
    rlQueue.add(node3);
    assertTrue(rlQueue.peekBest() == node3);
    rlQueue.add(node4);
    assertTrue(rlQueue.peekBest() == node3);
    rlQueue.add(node5);
    assertTrue(rlQueue.peekBest() == node5);
    assertTrue(rlQueue.collection().size() == 5);
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
    assertTrue(rlQueue.collection().size() == 5);
    assertTrue(rlQueue.pollBest() == node5);
    assertTrue(rlQueue.collection().size() == 4);
    assertTrue(rlQueue.pollBest() == node3);
    assertTrue(rlQueue.collection().size() == 3);
    assertTrue(rlQueue.pollBest() == node1);
    assertTrue(rlQueue.collection().size() == 2);
    assertTrue(rlQueue.pollBest() == node2);
    assertTrue(rlQueue.collection().size() == 1);
    assertTrue(rlQueue.pollBest() == node4);
    assertTrue(rlQueue.collection().isEmpty());
  }

  public void testRemoveAll() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    RelaxedGlobalQueue rlQueue = new RelaxedGlobalQueue(slacks);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(1, 1, 1), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node6 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    rlQueue.add(node1);
    rlQueue.add(node2);
    rlQueue.add(node3);
    rlQueue.add(node4);
    rlQueue.add(node5);
    List<GlcNode> removeList = new LinkedList<>();
    removeList.add(node1);
    assertTrue(rlQueue.collection().size() == 5);
    assertTrue(rlQueue.removeAll(removeList));
    removeList.clear();
    assertTrue(rlQueue.collection().size() == 4);
    assertFalse(rlQueue.collection().contains(node1));
    removeList.add(node2);
    removeList.add(node3);
    removeList.add(node4);
    removeList.add(node5);
    removeList.add(node6);
    assertTrue(rlQueue.removeAll(removeList));
    assertTrue(rlQueue.collection().isEmpty());
    assertFalse(rlQueue.removeAll(removeList));
  }

  // -------------- Test for abstract class RelaxedPriorityQueue --------------------
  public void testRemove() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    RelaxedGlobalQueue rlQueue = new RelaxedGlobalQueue(slacks);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    assertFalse(rlQueue.remove(node1));
    assertFalse(rlQueue.remove(node2));
    rlQueue.add(node1);
    assertFalse(rlQueue.remove(node2));
    assertTrue(rlQueue.collection().size() == 1);
    assertTrue(rlQueue.remove(node1));
  }
}
