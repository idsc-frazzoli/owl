// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class ProductTotalOrderTest extends TestCase {
  @SuppressWarnings("rawtypes")
  public void testEquals() {
    List<Comparable> x = new LinkedList<>();
    x.add(1);
    x.add("zwei");
    List<Comparable> y = new LinkedList<>();
    y.add(1);
    y.add("zwei");
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.EQUALS));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(PartialComparison.EQUALS));
  }

  @SuppressWarnings("rawtypes")
  public void testIncomparable() {
    List<Comparable> x = new LinkedList<>();
    x.add(true);
    x.add(1.34);
    List<Comparable> y = new LinkedList<>();
    y.add(false);
    y.add(3.56);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.INCOMPARABLE));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(PartialComparison.INCOMPARABLE));
  }

  @SuppressWarnings("rawtypes")
  public void testLessThan() {
    List<Comparable> x = new LinkedList<>();
    x.add(false);
    x.add(1.34);
    x.add(2);
    List<Comparable> y = new LinkedList<>();
    y.add(true);
    y.add(3.56);
    y.add(3);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.LESS_THAN));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(PartialComparison.GREATER_THAN));
  }

  @SuppressWarnings("rawtypes")
  public void testGreaterThan() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwei");
    x.add('a');
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    y.add('a');
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.GREATER_THAN));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(PartialComparison.LESS_THAN));
  }

  @SuppressWarnings("rawtypes")
  public void testException() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwei");
    x.add('a');
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
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
}
