// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

public enum Sl2Group implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public Sl2GroupElement element(Tensor xya) {
    return new Sl2GroupElement(xya);
  }
}
