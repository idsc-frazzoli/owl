// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.LieGroupElement;
import ch.ethz.idsc.tensor.Tensor;

public interface LieGroup {
  /** @param tensor
   * @return lie group element */
  LieGroupElement element(Tensor tensor);
}
