// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class LexicographicTotalOrderTest extends TestCase {
  public void testEquals() {
    List<Comparable> x = new LinkedList<>();
    x.add(1);
    x.add("zwei");
    List<Comparable> y = new LinkedList<>();
    y.add(1);
    y.add("zwei");
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, y) == 0);
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
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, y) == -1);
  }

  public void testGreaterThan() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwewwww");
    x.add(1);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    y.add(3);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, y) == 1);
  }

  public void testException() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwei");
    x.add(23);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    try {
      LexicographicTotalOrder.INSTANCE.compare(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
