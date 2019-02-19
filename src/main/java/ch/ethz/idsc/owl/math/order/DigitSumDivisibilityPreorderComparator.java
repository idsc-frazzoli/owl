// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Compares the digit sum of two integers according to divisibility,
 * e.g. the digit sum of of 123 is 6 and the digit sum of 47 is 11,
 * hence 123 and 47 are incomparable. */
public enum DigitSumDivisibilityPreorderComparator {
  ;
  static int digitSum(int x) {
    if (x < 0 || x == 0) {
      throw new RuntimeException("Only interested in digit sum of postive numbers");
    }
    int digitSum = 0;
    while (x > 0) {
      digitSum = digitSum + x % 10;
      x = x / 10;
    }
    return digitSum;
  }

  private static final BinaryRelation<Integer> BINARY_RELATION = (x, y) -> digitSum(y) % digitSum(x) == 0;
  public static final PreorderComparator<Integer> INSTANCE = Preorder.comparator(BINARY_RELATION);
}
