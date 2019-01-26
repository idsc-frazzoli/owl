// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import junit.framework.TestCase;

public class ProviderRankTest extends TestCase {
  public void testProviderRank() {
    Set<ProviderRank> set = new ConcurrentSkipListSet<>();
    set.add(ProviderRank.FALLBACK);
    set.add(ProviderRank.AUTONOMOUS);
    set.add(ProviderRank.EMERGENCY);
    set.add(ProviderRank.MANUAL);
    Iterator<ProviderRank> it = set.iterator();
    assertEquals(it.next(), ProviderRank.EMERGENCY);
    assertEquals(it.next(), ProviderRank.MANUAL);
    assertEquals(it.next(), ProviderRank.AUTONOMOUS);
    assertEquals(it.next(), ProviderRank.FALLBACK);
  }

  public void testSimple() {
    Queue<ProviderRank> queue = new PriorityQueue<>();
    queue.add(ProviderRank.EMERGENCY);
    queue.add(ProviderRank.FALLBACK);
    queue.add(ProviderRank.GODMODE);
    assertEquals(queue.poll(), ProviderRank.GODMODE);
  }

  public void testSet() {
    Set<Integer> csls = new ConcurrentSkipListSet<>();
    csls.add(10);
    csls.add(2);
    csls.add(5);
    csls.add(0);
    csls.add(5);
    // fails without arraylist
    assertEquals(new ArrayList<>(csls), Arrays.asList(0, 2, 5, 10));
  }

  public void testMorePriority() {
    Queue<ProviderRank> queue = new PriorityQueue<>();
    queue.add(ProviderRank.CALIBRATION);
    queue.add(ProviderRank.TESTING);
    queue.add(ProviderRank.MANUAL);
    queue.add(ProviderRank.EMERGENCY);
    queue.add(ProviderRank.FALLBACK);
    queue.add(ProviderRank.GODMODE);
    assertEquals(queue.poll(), ProviderRank.GODMODE);
    assertEquals(queue.poll(), ProviderRank.EMERGENCY);
    assertEquals(queue.poll(), ProviderRank.CALIBRATION);
    assertEquals(queue.poll(), ProviderRank.MANUAL);
    assertEquals(queue.poll(), ProviderRank.TESTING);
    assertEquals(queue.poll(), ProviderRank.FALLBACK);
  }
}
