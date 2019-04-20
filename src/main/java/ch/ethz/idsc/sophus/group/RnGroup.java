// code by jph
package ch.ethz.idsc.sophus.group;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Euclidean vector space, group action is addition, the neutral element is 0.
 * 
 * the implementation also covers the case R^1 where the elements are of type {@link Scalar}
 * (instead of a vector of length 1) */
public enum RnGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public RnGroupElement element(Tensor tensor) {
    return new RnGroupElement(Objects.requireNonNull(tensor));
  }
}
