// code by jph
package ch.ethz.idsc.sophus;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** parameterized geodesic in a space of tensors */
@FunctionalInterface
public interface TensorGeodesic {
  /** @param p
   * @param q
   * @return parametric curve that for input 0 gives p and for input 1 gives q */
  ScalarTensorFunction curve(Tensor p, Tensor q);
}
