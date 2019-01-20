// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;

public class SetPartialComparatorTest extends TestCase {
  static Collection<Integer> create(Integer... integers) {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(integers)));
  }

  Collection<Integer> A = create(1, 2, 3);
  Collection<Integer> B = create(1, 2, 3);
  Collection<Integer> C = create(1, 2);
  Collection<Integer> D = create(1, 2, 3, 8);
  Collection<Integer> E = create(5, 6, 7);

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
