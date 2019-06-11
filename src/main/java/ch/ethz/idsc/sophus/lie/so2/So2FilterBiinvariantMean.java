// code by ob, jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.lie.ScalarBiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;

/** Careful:
 * So2FilterBiinvariantMean is not strictly a biinvariant mean, because the
 * computation is not invariant under permutation of input points and weights */
public enum So2FilterBiinvariantMean implements ScalarBiinvariantMean {
  INSTANCE;
  // ---
  private static final Mod MOD = Mod.function(Pi.TWO, Pi.VALUE.negate());

  @Override // from ScalarBiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    int middle = (sequence.length() - 1) / 2;
    Scalar a0 = sequence.Get(middle);
    return MOD.apply(a0.subtract(AffineQ.require(weights).dot(sequence.map(a0::subtract).map(MOD))));
  }
}
