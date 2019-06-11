// code by jph, ob
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Skew;
import ch.ethz.idsc.sophus.lie.so2.So2CoveringBiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** no restrictions on input points from Covering SE(2), albeit isolated singularities exists
 * 
 * weights are required to be affine */
public enum Se2CoveringBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  // ---

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    Scalar amean = So2CoveringBiinvariantMean.INSTANCE.mean(sequence.get(Tensor.ALL, 2), weights);
    // make transformation s.t. mean rotation is zero and retransformation after taking mean
    return Se2Skew.mean(new Se2CoveringGroupElement(Tensors.of(ZERO, ZERO, amean)), sequence, weights);
  }
}
