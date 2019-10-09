// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface HermiteFilter {
  /** @param delta between two samples in control points
   * @param control
   * @return */
  TensorIteration string(Scalar delta, Tensor control);
}
