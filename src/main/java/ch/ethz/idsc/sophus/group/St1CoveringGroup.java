// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

/** the covering group of ST(1) is parameterized by R^2 */
public enum St1CoveringGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public St1CoveringGroupElement element(Tensor lambdat) {
    return new St1CoveringGroupElement(lambdat);
  }
}
