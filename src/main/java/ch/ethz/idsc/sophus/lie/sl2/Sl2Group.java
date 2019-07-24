// code by jph
package ch.ethz.idsc.sophus.lie.sl2;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Tensor;

public enum Sl2Group implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public Sl2GroupElement element(Tensor vector) {
    return new Sl2GroupElement(vector);
  }
}
