// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Tensor;

/** parameterized by R^2 x [-pi, pi) */
public enum Se2Group implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public Se2GroupElement element(Tensor xya) {
    return new Se2GroupElement(xya);
  }
}
