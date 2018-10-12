// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.LieGroupAction;
import ch.ethz.idsc.owl.math.map.Se2GroupAction;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2Group implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public LieGroupAction element(Tensor xya) {
    return new Se2GroupAction(xya);
  }
}
