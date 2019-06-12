// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** derived from BiinvariantMean but with return type {@link Scalar} */
@FunctionalInterface
public interface ScalarBiinvariantMean extends BiinvariantMean {
  @Override // from BiinvariantMean
  Scalar mean(Tensor sequence, Tensor weights);
}
