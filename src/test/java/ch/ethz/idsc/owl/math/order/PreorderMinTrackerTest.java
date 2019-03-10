// code by astoll
package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class PreorderMinTrackerTest extends TestCase {
  public void testDigestNotEmptyList() {
    PreorderComparator<Integer> preorderComparator = DigitSumDivisibilityPreorderComparator.INTEGER;
    PreorderMinTracker<Integer> digitSumDivisibility = PreorderMinTracker.withList(preorderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  public void testDigestNotEmptySet() {
    PreorderComparator<Integer> preorderComparator = DigitSumDivisibilityPreorderComparator.INTEGER;
    PreorderMinTracker<Integer> digitSumDivisibility = PreorderMinTracker.withSet(preorderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  public void testWithList() {
    PreorderComparator<Integer> preorderComparator = DigitSumDivisibilityPreorderComparator.INTEGER;
    PreorderMinTracker<Integer> digitSumDivisibility = PreorderMinTracker.withList(preorderComparator);
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
    PreorderComparator<Integer> preorderComparator = DigitSumDivisibilityPreorderComparator.INTEGER;
    PreorderMinTracker<Integer> digitSumDivisibility = PreorderMinTracker.withSet(preorderComparator);
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
    PreorderComparator<Integer> preorderComparator = DigitSumDivisibilityPreorderComparator.INTEGER;
    PreorderMinTracker<Integer> digitSumDivisibility = PreorderMinTracker.withList(preorderComparator);
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  public void testDuplicateEntriesSet() {
    PreorderComparator<Integer> preorderComparator = DigitSumDivisibilityPreorderComparator.INTEGER;
    PreorderMinTracker<Integer> digitSumDivisibility = PreorderMinTracker.withSet(preorderComparator);
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }
}
