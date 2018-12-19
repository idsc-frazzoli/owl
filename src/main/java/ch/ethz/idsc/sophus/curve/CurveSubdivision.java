// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.tensor.Tensor;

public interface CurveSubdivision {
  /** @param tensor
   * @return one round of subdivision of closed curve defined by given tensor */
  Tensor cyclic(Tensor tensor);

  /** @param tensor
   * @return one round of subdivision of non-closed curve defined by given tensor */
  Tensor string(Tensor tensor);
}
