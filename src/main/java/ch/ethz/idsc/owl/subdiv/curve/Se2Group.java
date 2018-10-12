// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2GroupElement;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2Group implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public Se2GroupElement element(Tensor xya) {
    return new Se2GroupElement(xya);
  }
}
