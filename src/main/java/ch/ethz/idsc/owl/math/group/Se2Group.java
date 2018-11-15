// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;

/** parameterized by R^2 x [-pi, pi) */
public enum Se2Group implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public Se2GroupElement element(Tensor xya) {
    return new Se2GroupElement(xya);
  }
}
