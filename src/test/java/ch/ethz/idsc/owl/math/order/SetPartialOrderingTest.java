package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import junit.framework.TestCase;

public class SetPartialOrderingTest extends TestCase {
  HashSet A = new HashSet<>(Arrays.asList(1, 2, 3));
  HashSet B = new HashSet<>(Arrays.asList(1, 2, 3));
  HashSet C = new HashSet<>(Arrays.asList(1, 2));
  HashSet D = new HashSet<>(Arrays.asList(1, 2, 3, 8));
  HashSet E = new HashSet<>(Arrays.asList(5, 6, 7));

  public void testEquals() {
    Optional<Integer> optional = SetPartialOrdering.INSTANCE.compare(A, B);
    assertTrue(optional.get() == 0);
  }

  public void testGreater() {
    Optional<Integer> optional = SetPartialOrdering.INSTANCE.compare(A, C);
    assertTrue(optional.get() == 1);
  }

  public void testLess() {
    Optional<Integer> optional = SetPartialOrdering.INSTANCE.compare(A, D);
    assertTrue(optional.get() == -1);
  }

  public void testNotComparable() {
    Optional<Integer> optional = SetPartialOrdering.INSTANCE.compare(A, E);
    assertFalse(optional.isPresent());
  }
}
