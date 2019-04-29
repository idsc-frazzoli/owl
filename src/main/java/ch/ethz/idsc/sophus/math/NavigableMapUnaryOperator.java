// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.NavigableMap;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface NavigableMapUnaryOperator {
  /** @param function that takes a navigable and processes it
   * @return navigable map */
  NavigableMap<Scalar, Tensor> apply(NavigableMap<Scalar, Tensor> navigableMap);
}
