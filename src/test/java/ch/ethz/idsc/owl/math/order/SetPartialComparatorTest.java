// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class SetPartialComparatorTest extends TestCase {
  static Set<Integer> create(Integer... integers) {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(integers)));
  }

  Set<Integer> A = create(1, 2, 3);
  Set<Integer> B = create(1, 2, 3);
  Set<Integer> C = create(1, 2);
  Set<Integer> D = create(1, 2, 3, 8);
  Set<Integer> E = create(5, 6, 7);

  public void testEquals() {
    PartialComparison optional = SetPartialComparator.INSTANCE.compare(A, B);
    assertEquals(optional, PartialComparison.EQUALS);
  }

  public void testGreater() {
    PartialComparison optional = SetPartialComparator.INSTANCE.compare(A, C);
    assertEquals(optional, PartialComparison.GREATER_THAN);
  }

  public void testLess() {
    PartialComparison optional = SetPartialComparator.INSTANCE.compare(A, D);
    assertEquals(optional, PartialComparison.LESS_THAN);
  }

  public void testNotComparable() {
    PartialComparison optional = SetPartialComparator.INSTANCE.compare(A, E);
    assertEquals(optional, PartialComparison.INCOMPARABLE);
  }
}
