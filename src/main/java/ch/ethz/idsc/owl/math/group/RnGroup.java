// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;

public enum RnGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public RnGroupElement element(Tensor tensor) {
    return new RnGroupElement(tensor);
  }
}
