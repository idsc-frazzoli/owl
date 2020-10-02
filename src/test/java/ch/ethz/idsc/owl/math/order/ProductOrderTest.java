// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.owl.math.AssertFail;
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
      AssertFail.of(() -> ProductOrder.intersect(orderComparison, null));
      AssertFail.of(() -> ProductOrder.intersect(null, orderComparison));
    }
  }
}