// code by astoll
package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class DigitSumDivisibilityPreorderComparatorTest extends TestCase {
  public void testEquals() {
    PreorderComparison preorderComparison1 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(321, 6);
    PreorderComparison preorderComparison2 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(2, 10001);
    PreorderComparison preorderComparison3 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(345, 543);
    PreorderComparison preorderComparison4 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(10002, 12);
    assertTrue(preorderComparison1.equals(PreorderComparison.INDIFFERENT));
    assertTrue(preorderComparison2.equals(PreorderComparison.INDIFFERENT));
    assertTrue(preorderComparison3.equals(PreorderComparison.INDIFFERENT));
    assertTrue(preorderComparison4.equals(PreorderComparison.INDIFFERENT));
  }

  public void testGreaterEqualsOnly() {
    PreorderComparison preorderComparison1 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(372, 6);
    PreorderComparison preorderComparison2 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(44, 10001);
    PreorderComparison preorderComparison3 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(553434, 543);
    PreorderComparison preorderComparison4 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(101892, 1);
    assertTrue(preorderComparison1.equals(PreorderComparison.GREATER_EQUALS_ONLY));
    assertTrue(preorderComparison2.equals(PreorderComparison.GREATER_EQUALS_ONLY));
    assertTrue(preorderComparison3.equals(PreorderComparison.GREATER_EQUALS_ONLY));
    assertTrue(preorderComparison4.equals(PreorderComparison.GREATER_EQUALS_ONLY));
  }

  public void testLessEqualsOnly() {
    PreorderComparison preorderComparison1 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(1, 6);
    PreorderComparison preorderComparison2 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(4, 70001);
    PreorderComparison preorderComparison3 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(2, 543);
    PreorderComparison preorderComparison4 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(101001, 33333);
    assertTrue(preorderComparison1.equals(PreorderComparison.LESS_EQUALS_ONLY));
    assertTrue(preorderComparison2.equals(PreorderComparison.LESS_EQUALS_ONLY));
    assertTrue(preorderComparison3.equals(PreorderComparison.LESS_EQUALS_ONLY));
    assertTrue(preorderComparison4.equals(PreorderComparison.LESS_EQUALS_ONLY));
  }

  public void testIncomparable() {
    PreorderComparison preorderComparison1 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(2, 3);
    PreorderComparison preorderComparison2 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(4, 80001);
    PreorderComparison preorderComparison3 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(2, 533);
    PreorderComparison preorderComparison4 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(10001, 333);
    assertTrue(preorderComparison1.equals(PreorderComparison.INCOMPARABLE));
    assertTrue(preorderComparison2.equals(PreorderComparison.INCOMPARABLE));
    assertTrue(preorderComparison3.equals(PreorderComparison.INCOMPARABLE));
    assertTrue(preorderComparison4.equals(PreorderComparison.INCOMPARABLE));
  }

  public void testNegativeAndZeroCase() {
    try {
      PreorderComparison preorderComparison1 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(0, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      PreorderComparison preorderComparison1 = DigitSumDivisibilityPreorderComparator.INSTANCE.compare(-3, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
