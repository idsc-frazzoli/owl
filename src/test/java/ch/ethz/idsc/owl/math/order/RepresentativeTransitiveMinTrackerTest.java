// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.owl.demo.order.DigitSumDivisibilityPreorder;
import junit.framework.TestCase;

public class RepresentativeTransitiveMinTrackerTest extends TestCase {
  public void testDigestNotEmptyList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withList(orderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  public void testDigestNotEmptySet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  public void testWithList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withList(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(122);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    assertTrue(digitSumDivisibility.getMinElements().contains(122));
    digitSumDivisibility.digest(426);
    assertFalse(digitSumDivisibility.getMinElements().contains(426));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  public void testWithSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(122);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    assertTrue(digitSumDivisibility.getMinElements().contains(122));
    digitSumDivisibility.digest(426);
    assertFalse(digitSumDivisibility.getMinElements().contains(426));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  public void testDuplicateEntriesList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withList(orderComparator);
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  public void testDuplicateEntriesSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  public void testOnlyPreoneRepresentativeList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withList(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(213);
    assertFalse(digitSumDivisibility.getMinElements().contains(213));
    digitSumDivisibility.digest(443);
    assertTrue(digitSumDivisibility.getMinElements().contains(443));
    digitSumDivisibility.digest(1111223);
    assertFalse(digitSumDivisibility.getMinElements().contains(1111223));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  public void testOnlyPreoneRepresentativeSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    RepresentativeTransitiveMinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(213);
    assertFalse(digitSumDivisibility.getMinElements().contains(213));
    digitSumDivisibility.digest(443);
    assertTrue(digitSumDivisibility.getMinElements().contains(443));
    digitSumDivisibility.digest(1111223);
    assertFalse(digitSumDivisibility.getMinElements().contains(1111223));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }
}
