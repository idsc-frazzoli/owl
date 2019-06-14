// code by ob, jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.lie.ScalarBiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Careful:
 * So2FilterBiinvariantMean is not strictly a biinvariant mean, because the
 * computation is not invariant under permutation of input points and weights
 * for sequences of length 3 or greater. */
public enum So2FilterBiinvariantMean implements ScalarBiinvariantMean {
  INSTANCE;
  // ---
  @Override // from ScalarBiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    // sequences of odd and even length are permitted
    int middle = sequence.length() / 2;
    Scalar a0 = sequence.Get(middle);
    return So2.MOD.apply(a0.subtract(AffineQ.require(weights).dot(sequence.map(a0::subtract).map(So2.MOD))));
  }
}
