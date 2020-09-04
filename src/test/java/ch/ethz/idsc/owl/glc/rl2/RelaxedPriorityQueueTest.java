// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import java.io.IOException;
import java.util.List;

import ch.ethz.idsc.owl.demo.order.ScalarTotalOrder;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import junit.framework.TestCase;

public class RelaxedPriorityQueueTest extends TestCase {
  public void testSTOSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(ScalarTotalOrder.INSTANCE);
  }

  public void testPeekNull() throws ClassNotFoundException, IOException {
    RelaxedPriorityQueue relaxedPriorityQueue = Serialization.copy( //
        RelaxedDomainQueue.empty(Tensors.vector(1, 2, 3)));
    assertNull(relaxedPriorityQueue.peekBest());
  }

  public void testDimensionsChengQiLu() {
    Tensor matrix = Array.zeros(300, 300);
    List<Integer> list = Dimensions.of(matrix);
    assertEquals(list.get(0), list.get(1));
    SquareMatrixQ.require(matrix);
  }

  public void testPollThrows() {
    RelaxedPriorityQueue relaxedPriorityQueue = RelaxedDomainQueue.empty(Tensors.vector(1, 2, 3));
    try {
      relaxedPriorityQueue.pollBest();
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
