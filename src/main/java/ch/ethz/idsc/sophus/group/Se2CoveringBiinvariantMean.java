// code by jph, ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/* package */ enum Se2CoveringBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  // ---

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    // FIXME JPH implementation is not verified yet!
    throw TensorRuntimeException.of(sequence, weights);
  }
}
