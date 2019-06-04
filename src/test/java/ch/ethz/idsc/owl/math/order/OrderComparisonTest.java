// code by jph
package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class OrderComparisonTest extends TestCase {
  public void testSimple() {
    assertEquals(OrderComparison.values().length, 4);
  }
}
