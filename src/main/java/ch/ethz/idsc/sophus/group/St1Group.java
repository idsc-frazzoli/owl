// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

/** parameterized by R^2 x [-pi, pi) */
public enum St1Group implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public St1GroupElement element(Tensor lambdat) {
    return new St1GroupElement(lambdat);
  }
}
