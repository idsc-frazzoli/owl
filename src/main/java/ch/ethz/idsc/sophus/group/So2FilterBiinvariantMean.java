// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;

/** Careful:
 * So2FilterBiinvariantMean is not strictly a biinvariant mean, because the
 * computation is not invariant under permutation of input points and weights */
/* package */ enum So2FilterBiinvariantMean implements ScalarBiinvariantMean {
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
