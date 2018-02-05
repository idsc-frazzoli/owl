// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Iterator;
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
}
