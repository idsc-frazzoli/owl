// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.num.IntegerDigits;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sign;

/** Compares the digit sum of two integers according to divisibility,
 * e.g. the digit sum of of 123 is 6 and the digit sum of 47 is 11,
 * hence 123 and 47 are incomparable. */
public enum DigitSumDivisibilityPreorderComparator {
  ;
  /** @param x strictly positive
   * @return */
  static Scalar totalDigits(Scalar x) {
    return Total.of(IntegerDigits.of(Sign.requirePositive(x))).Get();
  }

  private static final BinaryRelation<Scalar> BINARY_RELATION_SCALAR = //
      (x, y) -> Scalars.divides(totalDigits(x), totalDigits(y));
  /** for scalar */
  public static final PreorderComparator<Scalar> SCALAR = Preorder.comparator(BINARY_RELATION_SCALAR);
  // ---
  private static final BinaryRelation<Integer> BINARY_RELATION_INTEGER = //
      (x, y) -> Scalars.divides(totalDigits(RealScalar.of(x)), totalDigits(RealScalar.of(y)));
  /** for integers */
  public static final PreorderComparator<Integer> INTEGER = Preorder.comparator(BINARY_RELATION_INTEGER);
}
