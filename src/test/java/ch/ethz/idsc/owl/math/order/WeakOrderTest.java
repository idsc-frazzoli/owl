// code by jph
package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class WeakOrderTest extends TestCase {
  public void testFail() {
    WeakOrderComparator<Object> comparator = WeakOrder.comparator((a, b) -> false);
    try {
      comparator.compare(3, 4);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
