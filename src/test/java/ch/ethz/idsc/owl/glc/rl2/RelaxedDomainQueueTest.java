// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
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
    RelaxedPriorityQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    assertTrue(rlQueue.collection().contains(node1));
    rlQueue.add(node2);
    assertTrue(rlQueue.collection().contains(node1) && rlQueue.collection().contains(node2));
    assertTrue(rlQueue.add(node3).contains(node3));
    assertTrue(rlQueue.collection().contains(node1));
    assertTrue(rlQueue.collection().contains(node2));
    assertFalse(rlQueue.collection().contains(node3));
    assertTrue(rlQueue.add(node4).contains(node4));
    assertTrue(rlQueue.collection().contains(node1));
    assertTrue(rlQueue.collection().contains(node2));
    assertFalse(rlQueue.collection().contains(node3));
    assertFalse(rlQueue.collection().contains(node4));
    assertTrue(rlQueue.add(node5).contains(node1));
    assertTrue(rlQueue.collection().contains(node5));
    assertTrue(rlQueue.collection().contains(node2));
    assertFalse(rlQueue.collection().contains(node1) && rlQueue.collection().contains(node3) && rlQueue.collection().contains(node4));
  }

  public void testPeek() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(2, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(2, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 2, 2), VectorScalar.of(0, 0, 0));
    RelaxedPriorityQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    assertTrue(rlQueue.collection().contains(node1));
    assertTrue(rlQueue.peekBest() == node1);
    assertTrue(rlQueue.collection().size() == 1);
    rlQueue.add(node2);
    assertTrue(rlQueue.peekBest() == node2);
    assertTrue(rlQueue.collection().size() == 2);
    rlQueue.add(node3);
    assertTrue(rlQueue.peekBest() == node2);
    assertTrue(rlQueue.collection().size() == 2);
    rlQueue.add(node4);
    assertTrue(rlQueue.peekBest() == node2);
    assertTrue(rlQueue.collection().size() == 2);
    rlQueue.add(node5);
    assertTrue(rlQueue.peekBest() == node5);
    assertTrue(rlQueue.collection().size() == 2);
  }

  public void testPoll() throws ClassNotFoundException, IOException {
    Tensor slacks = Tensors.vector(3, 3, 3);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(1.5, 1.2, 1.5), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(0.5, 1.5, 1.5), VectorScalar.of(0, 0, 0));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0.5, 1.6, 1.4), VectorScalar.of(0, 0, 0));
    RelaxedPriorityQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    assertTrue(rlQueue.collection().size() == 1);
    rlQueue.add(node2);
    assertTrue(rlQueue.collection().size() == 2);
    rlQueue.add(node3);
    assertTrue(rlQueue.collection().size() == 3);
    rlQueue.add(node4);
    assertTrue(rlQueue.collection().size() == 4);
    rlQueue.add(node5);
    Serialization.copy(rlQueue);
    assertTrue(rlQueue.collection().size() == 5);
    assertTrue(rlQueue.pollBest() == node4);
    assertTrue(rlQueue.collection().size() == 4);
    assertTrue(rlQueue.pollBest() == node5);
    assertTrue(rlQueue.collection().size() == 3);
    assertTrue(rlQueue.pollBest() == node1);
    assertTrue(rlQueue.collection().size() == 2);
    assertTrue(rlQueue.pollBest() == node2);
    assertTrue(rlQueue.collection().size() == 1);
    assertTrue(rlQueue.pollBest() == node3);
    assertTrue(rlQueue.collection().isEmpty());
  }

  public void testEmpty() {
    Tensor slacks = Tensors.vector(3, 3, 3);
    RelaxedPriorityQueue rlQueue = RelaxedDomainQueue.empty(slacks);
    assertTrue(rlQueue.collection().isEmpty());
  }

  public void testSpeed() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    Random random = new Random();
    Scalar costFromRoot = VectorScalar.of(Tensors.vectorDouble(random.doubles(3, 1, 2).toArray()));
    Scalar minCostToGoal = VectorScalar.of(0, 0, 0);
    GlcNode firstNode = GlcNode.of(null, null, costFromRoot, minCostToGoal);
    RelaxedPriorityQueue relaxedPriorityQueue = RelaxedDomainQueue.singleton(firstNode, slacks);
    {
      Timing timing = Timing.started();
      int removed = 0;
      int limit = 1000;
      for (int count = 0; count < limit; ++count) {
        costFromRoot = VectorScalar.of(Tensors.vectorDouble(random.doubles(3, 1, 2).toArray()));
        GlcNode node = GlcNode.of(null, null, costFromRoot, minCostToGoal);
        Collection<GlcNode> collection = relaxedPriorityQueue.add(node);
        removed += collection.size();
      }
      double seconds = timing.seconds(); // 0.11995163700000001
      // System.out.println(seconds);
      assertTrue(seconds < 0.5);
      assertEquals(relaxedPriorityQueue.size() + removed, limit + 1);
    }
    {
      Timing timing = Timing.started();
      relaxedPriorityQueue.pollBest();
      double seconds = timing.seconds(); // 4.99146E-4
      // System.out.println(seconds);
      assertTrue(seconds < 0.01);
    }
  }
}
