// code by jph
package ch.ethz.idsc.owl.data;

import java.util.PriorityQueue;
import java.util.Queue;

import junit.framework.TestCase;

public class BoundedMaxQueueTest extends TestCase {
  public void testPriorityQueue() {
    PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
    priorityQueue.add(3);
    priorityQueue.add(1);
    priorityQueue.add(5);
    assertEquals(priorityQueue.poll().intValue(), 1);
    assertEquals(priorityQueue.poll().intValue(), 3);
    assertEquals(priorityQueue.poll().intValue(), 5);
  }

  public void testSimple() {
    Queue<Integer> queue = BoundedMaxQueue.of(3);
    assertTrue(queue.add(3));
    assertEquals(queue.size(), 1);
    assertTrue(queue.add(1));
    assertEquals(queue.size(), 2);
    assertTrue(queue.add(3));
    assertEquals(queue.size(), 3);
    assertTrue(queue.add(5));
    assertEquals(queue.size(), 3);
    assertFalse(queue.add(2));
    assertEquals(queue.size(), 3);
    assertTrue(queue.add(4));
    assertEquals(queue.size(), 3);
    assertEquals(queue.poll().intValue(), 3);
    assertEquals(queue.poll().intValue(), 4);
    assertEquals(queue.poll().intValue(), 5);
  }
}
