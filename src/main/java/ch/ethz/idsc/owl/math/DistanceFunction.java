// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DistanceFunction.html">DistanceFunction</a> */
@FunctionalInterface
public interface DistanceFunction<T> {
  /** @param element
   * @return non-negative distance to given element */
  Scalar distance(T element);
}
