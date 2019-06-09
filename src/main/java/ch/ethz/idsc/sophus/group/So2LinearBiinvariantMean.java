// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Mod;

/** Hint:
 * angles are required to lie on a half-circle which is not necessarily centered at the origin
 * 
 * Source for Constant C: https://hal.inria.fr/inria-00073318/
 * Xavier Pennec
 * TODO OB cite title, page no., and quote statement */
public enum So2LinearBiinvariantMean implements So2BiinvariantMean {
  INSTANCE;
  // ---
  private static final Mod MOD = Mod.function(Pi.TWO, Pi.VALUE.negate());

  @Override // from So2BiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    Scalar a0 = sequence.Get(0);
    Tensor shifted = sequence.map(a0::subtract).map(MOD);
    ScalarSummaryStatistics scalarSummaryStatistics = shifted.stream() //
        .map(Scalar.class::cast) //
        .collect(ScalarSummaryStatistics.collector());
    Scalar width = scalarSummaryStatistics.getMax().subtract(scalarSummaryStatistics.getMin());
    if (Scalars.lessEquals(Pi.VALUE, width))
      throw TensorRuntimeException.of(sequence);
    return MOD.apply(a0.subtract(weights.dot(shifted)));
  }
}
