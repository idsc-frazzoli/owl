// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class LexicographicTotalOrderTest extends TestCase {
  @SuppressWarnings("rawtypes")
  public void testEquals() {
    List<Comparable> x = new LinkedList<>();
    x.add(1);
    x.add("zwei");
    List<Comparable> y = new LinkedList<>();
    y.add(1);
    y.add("zwei");
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, x) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, y) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, y) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, x) == 0);
  }

  @SuppressWarnings("rawtypes")
  public void testLessThan() {
    List<Comparable> x = new LinkedList<>();
    x.add(true);
    x.add(3.56);
    x.add(2);
    List<Comparable> y = new LinkedList<>();
    y.add(true);
    y.add(3.56);
    y.add(3);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, x) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, y) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, y) == -1);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, x) == +1);
  }

  @SuppressWarnings("rawtypes")
  public void testGreaterThan() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwewwww");
    x.add(1);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    y.add(3);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, x) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, y) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, y) > 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, x) < 0);
  }

  @SuppressWarnings("rawtypes")
  public void testException() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwei");
    x.add(23);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, x) == 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, y) == 0);
    try {
      LexicographicTotalOrder.INSTANCE.compare(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      LexicographicTotalOrder.INSTANCE.compare(y, x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testEmpty() {
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(Arrays.asList(), Arrays.asList()), 0);
  }
}
