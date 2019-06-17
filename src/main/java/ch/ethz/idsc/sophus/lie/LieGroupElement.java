// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Tensor;

/** interface of an element of a Lie-group */
public interface LieGroupElement {
  /** @return inverse of this element */
  LieGroupElement inverse();

  /** @param tensor
   * @return group action of this element and the element defined by given tensor */
  Tensor combine(Tensor tensor);

  /** the adjoint map is a linear map on the lie algebra with full rank
   * 
   * @param tensor element of the lie algebra
   * @return Ad(this).tensor */
  Tensor adjoint(Tensor tensor);
}
