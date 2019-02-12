// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class ProductTotalOrderTest extends TestCase {
  public void testEquals() {
    List<Comparable> x = new LinkedList<>();
    x.add(1);
    x.add("zwei");
    List<Comparable> y = new LinkedList<>();
    y.add(1);
    y.add("zwei");
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.EQUALS));
  }

  public void testIncomparable() {
    List<Comparable> x = new LinkedList<>();
    x.add(true);
    x.add((double) 1.34);
    List<Comparable> y = new LinkedList<>();
    y.add(false);
    y.add((double) 3.56);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.INCOMPARABLE));
  }

  public void testLessThan() {
    List<Comparable> x = new LinkedList<>();
    x.add(false);
    x.add((double) 1.34);
    x.add(2);
    List<Comparable> y = new LinkedList<>();
    y.add(true);
    y.add((double) 3.56);
    y.add(3);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.LESS_THAN));
  }

  public void testGreaterThan() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwei");
    x.add(23);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    y.add(3);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(PartialComparison.GREATER_THAN));
  }

  public void testException() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwei");
    x.add(23);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    try {
      ProductTotalOrder.INSTANCE.compare(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
