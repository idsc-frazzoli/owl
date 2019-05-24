// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** a group element SO(2) is represented as a Scalar in [-pi, pi)
 * 
 * an element of the algebra so(2) is represented as 'vector' of length 1
 * (Actually a scalar, but LieExponential requires a vector) */
public enum So2Exponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Scalar exp(Tensor scalar) {
    return (Scalar) scalar;
  }

  @Override // from LieExponential
  public Scalar log(Tensor scalar) {
    return (Scalar) scalar;
  }
}
