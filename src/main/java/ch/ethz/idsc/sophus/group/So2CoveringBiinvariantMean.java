// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
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
    return AffineQ.require(weights).dot(sequence).Get();
  }
}
