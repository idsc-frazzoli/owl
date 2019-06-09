// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface So2BiinvariantMean extends BiinvariantMean {
  @Override
  Scalar mean(Tensor sequence, Tensor weights);
}
