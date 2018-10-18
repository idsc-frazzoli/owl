// code by jph
package ch.ethz.idsc.owl.math.group;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;

/** Euclidean vector space with addition
 * neutral element is 0 */
public enum RnGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public RnGroupElement element(Tensor tensor) {
    return new RnGroupElement(Objects.requireNonNull(tensor));
  }
}
