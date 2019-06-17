// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;

/** The weights are required to be affine. */
public enum RnBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  @Override // from BiinvariantMean
  public Tensor mean(Tensor sequence, Tensor weights) {
    return weights.dot(sequence);
  }
}
