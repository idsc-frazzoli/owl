// code by astoll
package ch.ethz.idsc.owl.demo.order;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import junit.framework.TestCase;

public class SetPartialOrderTest extends TestCase {
  static Collection<Integer> create(Integer... integers) {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(integers)));
  }

  Collection<Integer> A = create(1, 2, 3);
  Collection<Integer> B = create(1, 2, 3);
  Collection<Integer> C = create(1, 2);
  Collection<Integer> D = create(1, 2, 3, 8);
  Collection<Integer> E = create(5, 6, 7);

  public void testEquals() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, B);
    assertEquals(comparison, OrderComparison.INDIFFERENT);
  }

  public void testGreater() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, C);
    assertEquals(comparison, OrderComparison.STRICTLY_SUCCEEDS);
  }

  public void testLess() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, D);
    assertEquals(comparison, OrderComparison.STRICTLY_PRECEDES);
  }

  public void testNotComparable() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, E);
    assertEquals(comparison, OrderComparison.INCOMPARABLE);
  }
}
