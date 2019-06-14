// code by jph
package ch.ethz.idsc.sophus.lie.so2c;

import ch.ethz.idsc.sophus.lie.ScalarBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** The covering of SO(2) is isometric to R.
 * There are no restrictions on the input sequence.
 * 
 * @see RnBiinvariantMean */
public enum So2CoveringBiinvariantMean implements ScalarBiinvariantMean {
  INSTANCE;
  // ---
  @Override // from ScalarBiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    return weights.dot(sequence).Get();
  }
}
