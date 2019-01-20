// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

/** (n)-dimensional Scaling and Translations group */
public enum StGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override // from LieGroup
  public StGroupElement element(Tensor lambda_t) {
    return new StGroupElement(lambda_t);
  }
}
