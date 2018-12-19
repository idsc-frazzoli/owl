// code by jph
package ch.ethz.idsc.sophus.group;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;

/** Euclidean vector space
 * group action is addition
 * neutral element is 0 */
public enum RnGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public RnGroupElement element(Tensor tensor) {
    return new RnGroupElement(Objects.requireNonNull(tensor));
  }
}
