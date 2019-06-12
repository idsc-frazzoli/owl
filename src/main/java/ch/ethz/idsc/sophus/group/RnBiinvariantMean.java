// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;

/** The weights are required to be affine. */
public enum RnBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  @Override
  public Tensor mean(Tensor sequence, Tensor weights) {
    return AffineQ.require(weights).dot(sequence);
  }
}
