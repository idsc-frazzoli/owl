// code by jph
package ch.ethz.idsc.owl.math.flow;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** time invariant differential constraint
 * 
 * serializable */
// TODO investigate where at() and where getU() is used
public interface Flow extends Serializable {
  /** @param x
   * @return tangent of flow evaluated at x */
  // used 20 times 
  Tensor at(Tensor x);

  /** the control identifier is not a {@link Scalar}, but a vector encoded as a {@link Tensor}
   * 
   * @return unmodifiable identifier/control that determines the flow */
  // used 78 times
  Tensor getU();
}
