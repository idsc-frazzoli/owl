// code by astoll
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class DigitSumDivisibilityPreorderTest extends TestCase {
  public void testEquals() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(321, 6);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 10001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(345, 543);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(10002, 12);
    assertTrue(preorderComparison1.equals(OrderComparison.INDIFFERENT));
    assertTrue(preorderComparison2.equals(OrderComparison.INDIFFERENT));
    assertTrue(preorderComparison3.equals(OrderComparison.INDIFFERENT));
    assertTrue(preorderComparison4.equals(OrderComparison.INDIFFERENT));
  }
  public void testEqualsScalar() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.SCALAR.compare(RealScalar.of(321), RealScalar.of(6));
    assertTrue(preorderComparison1.equals(OrderComparison.INDIFFERENT));
  }

  public void testGreaterEqualsOnly() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(372, 6);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(44, 10001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(553434, 543);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(101892, 1);
    assertTrue(preorderComparison1.equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(preorderComparison2.equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(preorderComparison3.equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(preorderComparison4.equals(OrderComparison.STRICTLY_SUCCEEDS));
  }

  public void testLessEqualsOnly() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(1, 6);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(4, 70001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 543);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(101001, 33333);
    assertTrue(preorderComparison1.equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(preorderComparison2.equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(preorderComparison3.equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(preorderComparison4.equals(OrderComparison.STRICTLY_PRECEDES));
  }

  public void testIncomparable() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 3);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(4, 80001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 533);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(10001, 333);
    assertTrue(preorderComparison1.equals(OrderComparison.INCOMPARABLE));
    assertTrue(preorderComparison2.equals(OrderComparison.INCOMPARABLE));
    assertTrue(preorderComparison3.equals(OrderComparison.INCOMPARABLE));
    assertTrue(preorderComparison4.equals(OrderComparison.INCOMPARABLE));
  }

  public void testNegativeAndZeroCase() {
    try {
      OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(0, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(-3, 3);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
