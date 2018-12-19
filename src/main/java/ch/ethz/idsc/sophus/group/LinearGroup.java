// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

/** Lie group GL(n) of invertible square matrices */
public enum LinearGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public LinearGroupElement element(Tensor matrix) {
    return new LinearGroupElement(matrix);
  }
}
