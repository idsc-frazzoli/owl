// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface BiinvariantMeanInterface {
  /** @param sequence of Lie Group elements
   * @param corresponding non-negative, affine weights
   * @return biinvariant mean */
  Tensor mean(Tensor sequence, Tensor weights);
}
