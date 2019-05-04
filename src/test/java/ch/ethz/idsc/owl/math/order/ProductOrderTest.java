// code by astoll
package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class ProductOrderTest extends TestCase {
  public void testSimple() {
    assertEquals(ProductOrder.intersect(OrderComparison.INDIFFERENT, OrderComparison.STRICTLY_PRECEDES), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(ProductOrder.intersect(OrderComparison.INDIFFERENT, OrderComparison.INCOMPARABLE), OrderComparison.INCOMPARABLE);
    assertEquals(ProductOrder.intersect(OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_SUCCEEDS), OrderComparison.INCOMPARABLE);
    assertEquals(ProductOrder.intersect(OrderComparison.STRICTLY_SUCCEEDS, OrderComparison.STRICTLY_SUCCEEDS), OrderComparison.STRICTLY_SUCCEEDS);
  }
}