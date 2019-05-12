// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** @author Andre
 *
 * Searches a totally ordered set for its minimal or maximal element */
/* package */ enum TotalOrderMinMax {
  ;
  /** @param Totally ordered, non-empty set to be searched for minimal element
   * @return Minimal element of totally ordered set */
  public static Scalar min(Tensor tensor) {
    return tensor.stream().map(Scalar.class::cast).min(Scalars::compare).get();
  }

  /** @param Totally ordered, non-empty set to be searched for maximal element
   * @return Maximal element of totally ordered set */
  public static Scalar max(Tensor tensor) {
    return tensor.stream().map(Scalar.class::cast).max(Scalars::compare).get();
  }
}