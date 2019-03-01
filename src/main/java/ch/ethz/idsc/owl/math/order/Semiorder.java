// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public enum Semiorder {
  ;
  public static <T> StrictPartialComparator<T> comparator(UtilityFunction<T, Scalar> utilityFunction, Scalar slack) {
    return new StrictPartialComparator<T>() {
      @Override // from StrictPartialComparison
      public StrictPartialComparison compare(T x, T y) {
        Scalar utility_x = utilityFunction.apply(x);
        Scalar utility_y = utilityFunction.apply(y);
        if (Scalars.lessEquals(utility_x.add(slack), utility_y)) {
          return StrictPartialComparison.LESS_THAN;
        }
        if (Scalars.lessEquals(utility_y.add(slack), utility_x)) {
          return StrictPartialComparison.GREATER_THAN;
        }
        return StrictPartialComparison.INCOMPARABLE;
      }
    };
  }
}