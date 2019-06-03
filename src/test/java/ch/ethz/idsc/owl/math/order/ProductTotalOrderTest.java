// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class ProductTotalOrderTest extends TestCase {
  @SuppressWarnings("rawtypes")
  public void testEquals() {
    List<Comparable> x = Arrays.asList(1, "zwei");
    List<Comparable> y = Arrays.asList(1, "zwei");
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.INDIFFERENT));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.INDIFFERENT));
  }

  @SuppressWarnings("rawtypes")
  public void testIncomparable() {
    List<Comparable> x = Arrays.asList(true, 1.34);
    List<Comparable> y = Arrays.asList(false, 3.56);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.INCOMPARABLE));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.INCOMPARABLE));
  }

  @SuppressWarnings("rawtypes")
  public void testIncomparable3() {
    List<Comparable> x = Arrays.asList(2, true, 1.34);
    List<Comparable> y = Arrays.asList(2, false, 3.56);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.INCOMPARABLE));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.INCOMPARABLE));
  }

  @SuppressWarnings("rawtypes")
  public void testLessThan() {
    List<Comparable> x = Arrays.asList(false, 1.34, 2);
    List<Comparable> y = Arrays.asList(true, 3.56, 2);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.STRICTLY_SUCCEEDS));
  }

  @SuppressWarnings("rawtypes")
  public void testGreaterThan() {
    List<Comparable> x = Arrays.asList("zwei", 'a');
    List<Comparable> y = Arrays.asList("drei", 'a');
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.STRICTLY_PRECEDES));
  }

  public void testEmpty() {
    OrderComparison orderComparison = ProductTotalOrder.INSTANCE.compare(Arrays.asList(), Arrays.asList());
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }

  @SuppressWarnings("rawtypes")
  public void testSizeException() {
    List<Comparable> x = Arrays.asList("zwei", 'a');
    List<Comparable> y = Arrays.asList("drei");
    try {
      ProductTotalOrder.INSTANCE.compare(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ProductTotalOrder.INSTANCE.compare(y, x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullException() {
    try {
      ProductTotalOrder.INSTANCE.compare(Arrays.asList(2), null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ProductTotalOrder.INSTANCE.compare(null, Arrays.asList(2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
