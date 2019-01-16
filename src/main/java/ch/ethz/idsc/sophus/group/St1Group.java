// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

/** (1)-dimensional Scaling and Translations group */
public enum St1Group implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public St1GroupElement element(Tensor lambdat) {
    return new St1GroupElement(lambdat);
  }
}
