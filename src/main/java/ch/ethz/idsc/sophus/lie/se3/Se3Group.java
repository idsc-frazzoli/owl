// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Tensor;

/** g is a 4 x 4 matrix in SE(3) */
public enum Se3Group implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public Se3GroupElement element(Tensor g) {
    return new Se3GroupElement(g);
  }
}
