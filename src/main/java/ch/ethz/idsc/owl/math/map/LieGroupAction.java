// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;

/** action of an element of a Lie-group */
public interface LieGroupAction {
  /** @param tensor
   * @return this element . tensor */
  Tensor combine(Tensor tensor);

  /** @return inverse of this element */
  Tensor inverse();
}
