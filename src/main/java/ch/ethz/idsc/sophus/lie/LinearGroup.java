// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Tensor;

/** Lie group GL(n) of invertible square matrices
 * also called "immersely linear Lie group" */
public enum LinearGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public LinearGroupElement element(Tensor matrix) {
    return LinearGroupElement.of(matrix);
  }
}
