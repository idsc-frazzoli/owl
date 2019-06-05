// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;

/** Biinvariant Mean is invariant under reordering of the input points and weights */
@FunctionalInterface
public interface BiinvariantMeanInterface {
  /** @param points in Lie Group
   * @param weights affine, typically non-negative
   * @return biinvariant mean */
  Tensor mean(Tensor points, Tensor weights);
}
