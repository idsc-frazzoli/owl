// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClipStrictPartialOrderTest extends TestCase {
  public void testIncomparable() {
    OrderComparison orderComparison1 = ClipStrictPartialOrder.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(0, 1));
    OrderComparison orderComparison2 = ClipStrictPartialOrder.INSTANCE.compare(Clips.interval(-1, 1), Clips.interval(0, 2));
    OrderComparison orderComparison3 = ClipStrictPartialOrder.INSTANCE.compare(Clips.interval(0, 5), Clips.interval(2, 3));
    OrderComparison orderComparison4 = ClipStrictPartialOrder.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(1, 2));
    assertTrue(orderComparison1.equals(OrderComparison.INCOMPARABLE));
    assertTrue(orderComparison2.equals(OrderComparison.INCOMPARABLE));
    assertTrue(orderComparison3.equals(OrderComparison.INCOMPARABLE));
    assertTrue(orderComparison4.equals(OrderComparison.INCOMPARABLE));
  }

  public void testLessThan() {
    OrderComparison orderComparison = ClipStrictPartialOrder.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(2, 3));
    assertTrue(orderComparison.equals(OrderComparison.STRICTLY_PRECEDES));
  }

  public void testGreaterThan() {
    OrderComparison orderComparison = ClipStrictPartialOrder.INSTANCE.compare(Clips.interval(4, 6), Clips.interval(0, 1));
    assertTrue(orderComparison.equals(OrderComparison.STRICTLY_SUCCEEDS));
  }

  public void testSimple() {
    OrderComparison orderComparison = //
        ClipStrictPartialOrder.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(0, 1));
    assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
  }
}
