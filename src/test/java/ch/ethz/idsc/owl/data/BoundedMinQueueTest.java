// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Queue;

import junit.framework.TestCase;

public class BoundedMinQueueTest extends TestCase {
  public void testPriorityQueue() {
    PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
    priorityQueue.offer(3);
    priorityQueue.offer(1);
    priorityQueue.offer(5);
    assertEquals(priorityQueue.poll().intValue(), 5);
    assertEquals(priorityQueue.poll().intValue(), 3);
    assertEquals(priorityQueue.poll().intValue(), 1);
  }

  public void testSimple() {
    Queue<Integer> queue = BoundedMinQueue.of(3);
    assertTrue(queue.offer(3));
    assertEquals(queue.size(), 1);
    assertTrue(queue.offer(1));
    assertEquals(queue.size(), 2);
    assertTrue(queue.offer(3));
    assertEquals(queue.size(), 3);
    assertFalse(queue.offer(5));
    assertEquals(queue.size(), 3);
    assertTrue(queue.offer(2));
    assertEquals(queue.size(), 3);
    assertFalse(queue.offer(4));
    assertEquals(queue.size(), 3);
    assertEquals(queue.poll().intValue(), 3);
    assertEquals(queue.poll().intValue(), 2);
    assertEquals(queue.poll().intValue(), 1);
  }
}
