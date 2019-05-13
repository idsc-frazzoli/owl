// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RelaxedPriorityQueueTest extends TestCase {
  public void testPeekNull() {
    RelaxedPriorityQueue relaxedPriorityQueue = RelaxedDomainQueue.empty(Tensors.vector(1, 2, 3));
    assertNull(relaxedPriorityQueue.peekBest());
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
