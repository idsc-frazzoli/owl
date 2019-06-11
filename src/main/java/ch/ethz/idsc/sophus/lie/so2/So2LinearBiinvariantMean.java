// code by ob, jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.lie.ScalarBiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;

/** Hint:
 * angles are required to lie on a half-circle which is not necessarily centered at the origin
 * 
 * Source for Constant C: https://hal.inria.fr/inria-00073318/
 * Xavier Pennec
 * TODO OB cite title, page no., and quote statement */
public enum So2LinearBiinvariantMean implements ScalarBiinvariantMean {
  INSTANCE;
  // ---
  private static final Mod MOD = Mod.function(Pi.TWO, Pi.VALUE.negate());

  @Override // from ScalarBiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    Scalar a0 = sequence.Get(0);
    return MOD.apply(a0.subtract(AffineQ.require(weights).dot(So2Helper.rangeQ(sequence.map(a0::subtract).map(MOD)))));
  }
}
