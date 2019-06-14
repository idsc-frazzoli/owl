// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/** Hint: non-linear, non conform with {@link SnGeodesic} */
public enum SnGlobalBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  @Override // from BiinvariantMean
  public Tensor mean(Tensor sequence, Tensor weights) {
    return NORMALIZE.apply(weights.dot(sequence));
  }
}
