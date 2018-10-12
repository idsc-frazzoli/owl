// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.LieGroupAction;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2CoveringGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public LieGroupAction element(Tensor xya) {
    return new Se2CoveringGroupAction(xya);
  }
}
