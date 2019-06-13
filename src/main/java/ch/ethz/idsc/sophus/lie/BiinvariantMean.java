// code by ob
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.Tensor;

/** The biinvariant mean is invariant under simultaneous reordering of the input points and weights */
@FunctionalInterface
public interface BiinvariantMean {
  /** @param sequence of points in Lie group
   * @param weights vector typically affine, and non-negative
   * @return biinvariant mean
   * @see AffineQ */
  Tensor mean(Tensor sequence, Tensor weights);
}
