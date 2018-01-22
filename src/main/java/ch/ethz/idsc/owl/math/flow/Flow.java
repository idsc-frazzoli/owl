// code by jph
package ch.ethz.idsc.owl.math.flow;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** time invariant differential constraint
 * 
 * serializable */
public interface Flow extends Serializable {
  /** @param x
   * @return tangent of flow evaluated at x */
  Tensor at(Tensor x);

  /** the control identifier is not a {@link Scalar}, but a vector encoded as a {@link Tensor}
   * 
   * @return unmodifiable identifier/control that determines the flow */
  Tensor getU();
}
