// code by jph
package ch.ethz.idsc.sophus.lie;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum BiinvariantMeans {
  ;
  /** @param biinvariantMean
   * @param weights
   * @return
   * @throws Exception if given weights is not an affine vector */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, Tensor weights) {
    Objects.requireNonNull(biinvariantMean);
    AffineQ.require(weights);
    return sequence -> biinvariantMean.mean(sequence, weights);
  }
}
