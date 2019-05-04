// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import junit.framework.TestCase;

public class EqualityOrderTest extends TestCase {
  public void testInteger() {
    assertEquals(EqualityOrder.INSTANCE.compare(2, 3), OrderComparison.INCOMPARABLE);
    assertEquals(EqualityOrder.INSTANCE.compare(2, 2), OrderComparison.INDIFFERENT);
  }

  public void testObject() {
    assertEquals(EqualityOrder.INSTANCE.compare("asd", 3), OrderComparison.INCOMPARABLE);
    assertEquals(EqualityOrder.INSTANCE.compare(2, "asdd"), OrderComparison.INCOMPARABLE);
  }

  public void testNullFail() {
    try {
      EqualityOrder.INSTANCE.compare(null, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      EqualityOrder.INSTANCE.compare("abc", null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      EqualityOrder.INSTANCE.compare(null, "abc");
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
