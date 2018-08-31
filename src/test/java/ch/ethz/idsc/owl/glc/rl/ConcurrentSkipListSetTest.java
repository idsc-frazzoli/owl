// code by jph
package ch.ethz.idsc.owl.glc.rl;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import junit.framework.TestCase;

class IntWrap implements Comparable<IntWrap> {
  final int a;

  public IntWrap(int a) {
    this.a = a;
  }

  public int getValue() {
    return a;
  }

  @Override // from Comparable
  public int compareTo(IntWrap intWrap) {
    return Integer.compare(a, intWrap.a);
  }
}

// attempt to understand java class
public class ConcurrentSkipListSetTest extends TestCase {
  public void testSimple() {
    ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();
    set.add(2000);
    set.add(2);
    set.add(2000);
    set.add(2);
    assertEquals(set.size(), 2);
  }

  public void testObject() {
    ConcurrentSkipListSet<IntWrap> set = new ConcurrentSkipListSet<>();
    set.add(new IntWrap(3));
    set.add(new IntWrap(10));
    set.add(new IntWrap(3));
    set.add(new IntWrap(10));
    assertEquals(set.size(), 2);
  }

  public void testPriority() {
    PriorityQueue<IntWrap> priorityQueue = new PriorityQueue<>();
    priorityQueue.add(new IntWrap(3));
    priorityQueue.add(new IntWrap(10));
    priorityQueue.add(new IntWrap(3));
    priorityQueue.add(new IntWrap(10));
    List<Integer> list = priorityQueue.stream().map(IntWrap::getValue).collect(Collectors.toList());
    System.out.println(list);
    for (IntWrap iw : priorityQueue) {
      System.out.println(iw.getValue());
    }
    while (!priorityQueue.isEmpty()) {
      IntWrap poll = priorityQueue.poll();
      System.out.println(poll.a);
    }
  }
}
