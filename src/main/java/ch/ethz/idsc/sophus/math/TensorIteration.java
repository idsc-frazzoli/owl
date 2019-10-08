// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface TensorIteration {
  /** @return result of next step of tensor iteration */
  Tensor iterate();
}
