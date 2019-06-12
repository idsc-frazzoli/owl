// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Tensor;

/** (2*n+1)-dimensional Heisenberg group */
public enum HeGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public HeGroupElement element(Tensor xyz) {
    return new HeGroupElement(xyz);
  }
}
