// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;

public enum So3Group implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public LieGroupElement element(Tensor matrix) {
    return new So3GroupElement(matrix);
  }
}
