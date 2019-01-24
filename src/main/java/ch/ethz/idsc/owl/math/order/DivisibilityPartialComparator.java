// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public enum DivisibilityPartialComparator {
  ;
  public static final PartialComparator<Scalar> INSTANCE = PartialOrder.comparator(Scalars::divides);
}
