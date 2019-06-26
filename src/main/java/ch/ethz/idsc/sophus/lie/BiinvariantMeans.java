// code by jph
package ch.ethz.idsc.sophus.lie;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.AffineQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum BiinvariantMeans {
  ;
  /** @param biinvariantMean non-null
   * @param weights affine vector
   * @return
   * @throws Exception if given weights is not an affine vector */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, Tensor weights) {
    Objects.requireNonNull(biinvariantMean);
    AffineQ.require(weights);
    return sequence -> biinvariantMean.mean(sequence, weights);
  }
}
