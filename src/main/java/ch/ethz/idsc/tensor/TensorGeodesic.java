// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** parameterized geodesic in a space of tensors */
public interface TensorGeodesic {
  /** @param p
   * @param q
   * @return parametric curve that gives p for input 0 and q for intput 1 */
  ScalarTensorFunction curve(Tensor p, Tensor q);
}
