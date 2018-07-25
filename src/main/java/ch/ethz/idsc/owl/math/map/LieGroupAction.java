// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;

/** action of an element of a Lie-group */
public interface LieGroupAction {
  /** @return inverse action of this element */
  LieGroupAction inverse();

  /** @param tensor
   * @return this element . tensor */
  Tensor combine(Tensor tensor);
}
