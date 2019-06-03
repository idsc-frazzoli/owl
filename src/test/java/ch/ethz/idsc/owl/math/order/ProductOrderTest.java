// code by astoll
package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class ProductOrderTest extends TestCase {
  private static void _checkSym(OrderComparison a, OrderComparison b, OrderComparison ab) {
    assertEquals(ProductOrder.intersect(a, b), ab);
    assertEquals(ProductOrder.intersect(b, a), ab);
  }

  public void testSimple() {
    _checkSym(OrderComparison.INDIFFERENT, OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_PRECEDES);
    _checkSym(OrderComparison.INDIFFERENT, OrderComparison.INCOMPARABLE, OrderComparison.INCOMPARABLE);
    _checkSym(OrderComparison.INCOMPARABLE, OrderComparison.INCOMPARABLE, OrderComparison.INCOMPARABLE);
    _checkSym(OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_PRECEDES);
    _checkSym(OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_SUCCEEDS, OrderComparison.INCOMPARABLE);
    _checkSym(OrderComparison.STRICTLY_SUCCEEDS, OrderComparison.STRICTLY_SUCCEEDS, OrderComparison.STRICTLY_SUCCEEDS);
  }

  public void testNullFail() {
    for (OrderComparison orderComparison : OrderComparison.values()) {
      try {
        ProductOrder.intersect(orderComparison, null);
        fail();
      } catch (Exception exception) {
        // ---
      }
      try {
        ProductOrder.intersect(null, orderComparison);
        fail();
      } catch (Exception exception) {
        // ---
      }
    }
  }
}