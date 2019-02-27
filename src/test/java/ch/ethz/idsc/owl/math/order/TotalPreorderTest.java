// code by jph
package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class TotalPreorderTest extends TestCase {
  public void testFail() {
    TotalPreorderComparator<Object> comparator = TotalPreorder.comparator((a, b) -> false);
    try {
      comparator.compare(3, 4);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
