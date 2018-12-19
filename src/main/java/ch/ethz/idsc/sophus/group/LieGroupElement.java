// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

/** interface of an element of a Lie-group */
public interface LieGroupElement {
  /** @return inverse of this element */
  LieGroupElement inverse();

  /** @param tensor
   * @return group action of this element and the element defined by given tensor */
  Tensor combine(Tensor tensor);
}
