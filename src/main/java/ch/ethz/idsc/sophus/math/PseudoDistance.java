// code by ob, jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface PseudoDistance {
  /** @param p
   * @param q
   * @return */
  Tensor pseudoDistance(Tensor p, Tensor q);
}
