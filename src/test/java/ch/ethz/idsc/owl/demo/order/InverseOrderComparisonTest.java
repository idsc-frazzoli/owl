// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.InverseOrderComparison;
import ch.ethz.idsc.owl.math.order.OrderComparison;
import junit.framework.TestCase;

public class InverseOrderComparisonTest extends TestCase {
  public void testSimple() {
    assertEquals(InverseOrderComparison.of(OrderComparison.STRICTLY_PRECEDES), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(InverseOrderComparison.of(OrderComparison.INDIFFERENT), OrderComparison.INDIFFERENT);
    assertEquals(InverseOrderComparison.of(OrderComparison.STRICTLY_SUCCEEDS), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(InverseOrderComparison.of(OrderComparison.INCOMPARABLE), OrderComparison.INCOMPARABLE);
  }
}
