// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Divisible;
import ch.ethz.idsc.tensor.Scalar;

public enum DivisibilityPartialComparator {
  ;
  public static final PartialComparator<Scalar> INSTANCE = //
      PartialOrder.comparator((x, y) -> Divisible.of(y, x));
}
