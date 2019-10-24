// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import junit.framework.TestCase;

public class RLQueueTest extends TestCase {
  public void testSimple() {
    Tensor slack = Tensors.vector(1, 0, 0);
    RLQueue rlQueue = new RLQueue(slack);
    GlcNode node21 = GlcNode.of(null, null, VectorScalar.of(2, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node12 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node22 = GlcNode.of(null, null, VectorScalar.of(2, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node23 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node32 = GlcNode.of(null, null, VectorScalar.of(3, 2, 3), VectorScalar.of(0, 0, 0));
    GlcNode node31 = GlcNode.of(null, null, VectorScalar.of(3, 1, 3), VectorScalar.of(0, 0, 0));
    // ---
    rlQueue.add(node21);
    rlQueue.add(node12);
    rlQueue.add(node22);
    rlQueue.add(node23);
    rlQueue.add(node32);
    rlQueue.add(node31);
    // ---
    GlcNode best;
    best = rlQueue.poll();
    assertTrue(best.merit() == node21.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node12.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node31.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node22.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node32.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node23.merit());
    assertTrue(rlQueue.isEmpty());
  }

  public void testSpeed() {
    Tensor slack = Tensors.vector(1, 1, 1);
    RLQueue rlQueue = new RLQueue(slack);
    Random random = new Random();
    Scalar minCostToGoal = VectorScalar.of(0, 0, 0);
    {
      Timing timing = Timing.started();
      for (int i = 0; i < 1000; ++i) {
        Scalar costFromRoot = VectorScalar.of(Tensors.vectorDouble(random.doubles(3, 1, 2).toArray()));
        GlcNode node = GlcNode.of(null, null, costFromRoot, minCostToGoal);
        boolean added = rlQueue.add(node);
        assertTrue(added);
      }
      double seconds = timing.seconds(); // 0.045515109000000005
      assertTrue(seconds < 0.1);
    }
    {
      Timing timing = Timing.started();
      rlQueue.poll();
      double seconds = timing.seconds(); // 0.007376575000000001
      assertTrue(seconds < 0.05);
    }
  }

  public void testFailCollectionsMinEmpty() {
    try {
      Collections.min(Arrays.asList());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
