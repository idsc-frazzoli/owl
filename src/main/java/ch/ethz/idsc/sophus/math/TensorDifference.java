// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface TensorDifference {
  /** @param p
   * @param q
   * @return action to get from p to q, for instance log(p^-1.q), or q-p */
  Tensor difference(Tensor p, Tensor q);
}
