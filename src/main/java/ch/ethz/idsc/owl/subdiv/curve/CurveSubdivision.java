// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Tensor;

public interface CurveSubdivision {
  /** @param tensor
   * @return one round of subdivision of curve defined by given tensor */
  Tensor cyclic(Tensor tensor);
}
