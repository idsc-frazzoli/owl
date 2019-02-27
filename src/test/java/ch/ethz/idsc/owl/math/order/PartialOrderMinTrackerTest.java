// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class PartialOrderMinTrackerTest<E> extends TestCase {
  public void testDigestNotEmpty() {
    PartialComparator<Scalar> partialComparator = DivisibilityPartialComparator.INSTANCE;
    PartialOrderMinTracker<Scalar> divisibility = PartialOrderMinTracker.withList(partialComparator);
    divisibility.digest(RealScalar.of(6));
    assertFalse(divisibility.getMinElements().isEmpty());
  }

  public void testDigestFunction() {
    PartialComparator<Scalar> partialComparator = PartialOrder.comparator(Scalars::divides);
    PartialOrderMinTracker<Scalar> divisibility = PartialOrderMinTracker.withList(partialComparator);
    divisibility.digest(RealScalar.of(10));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(10)));
    divisibility.digest(RealScalar.of(2));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertFalse(divisibility.getMinElements().contains(RealScalar.of(10)));
    divisibility.digest(RealScalar.of(3));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(3)));
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(6));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(3)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(7)));
    assertFalse(divisibility.getMinElements().contains(RealScalar.of(6)));
  }

  public void testDuplicateEntries() {
    PartialComparator<Scalar> partialComparator = PartialOrder.comparator(Scalars::divides);
    PartialOrderMinTracker<Scalar> divisibility = PartialOrderMinTracker.withList(partialComparator);
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(7));
    assertEquals(divisibility.getMinElements().size(), 1);
  }

  public void testWithSet() {
    PartialComparator<Scalar> partialComparator = PartialOrder.comparator(Scalars::divides);
    PartialOrderMinTracker<Scalar> divisibility = PartialOrderMinTracker.withSet(partialComparator);
    divisibility.digest(RealScalar.of(6));
    assertFalse(divisibility.getMinElements().isEmpty());
  }
  
  @SuppressWarnings("rawtypes")
  public void testMinTracker() {
    PartialOrderMinTracker<List<Comparable>> partialOrderMinTracker = PartialOrderMinTracker.withList(ProductTotalOrder.INSTANCE);
    partialOrderMinTracker.digest(Arrays.asList("abc", 123));
    partialOrderMinTracker.digest(Arrays.asList("cde", 930));
    Collection<List<Comparable>> minElements = partialOrderMinTracker.getMinElements();
    assertTrue(minElements.contains(Arrays.asList("abc",123)));
  }
}
