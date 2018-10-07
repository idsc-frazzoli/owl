// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.subare.util.GlobalAssert;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
    GlobalAssert.that(best.merit() == node21.merit());
    best = rlQueue.poll();
    GlobalAssert.that(best.merit() == node12.merit());
    best = rlQueue.poll();
    GlobalAssert.that(best.merit() == node31.merit());
    best = rlQueue.poll();
    GlobalAssert.that(best.merit() == node22.merit());
    best = rlQueue.poll();
    GlobalAssert.that(best.merit() == node32.merit());
    best = rlQueue.poll();
    GlobalAssert.that(best.merit() == node23.merit());
    GlobalAssert.that(rlQueue.isEmpty());
  }

  public void testSpeed() {
    Tensor slack = Tensors.vector(1, 0, 0);
    RLQueue rlQueue = new RLQueue(slack);
    Random random = new Random();
    for (int i = 0; i < 1000; ++i) {
      Scalar costFromRoot = VectorScalar.of(Tensors.vectorDouble(random.doubles(3, 1, 2).toArray()));
      Scalar minCostToGoal = VectorScalar.of(0, 0, 0);
      GlcNode node = GlcNode.of(null, null, costFromRoot, minCostToGoal);
      rlQueue.add(node);
    }
    Stopwatch sw = Stopwatch.started();
    rlQueue.poll();
    System.out.println(sw.display_seconds());
  }

  public void testCollectionMin() {
    try {
      Collections.min(Arrays.asList());
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
