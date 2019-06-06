// code by jph
package ch.ethz.idsc.sophus;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface TensorNorm {
  // TODO JPH documentation
  /** @param tensor
   * @return */
  Scalar norm(Tensor tensor);
}
