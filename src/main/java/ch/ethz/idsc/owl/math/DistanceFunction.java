// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;

public interface DistanceFunction<T> {
  /** @param element
   * @return non-negative distance to given element */
  Scalar distance(T element);
}
