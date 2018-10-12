// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.owl.subdiv.curve.LieGroup;
import ch.ethz.idsc.tensor.Tensor;

public enum RnGroup implements LieGroup {
  INSTANCE;
  // ---
  @Override
  public LieGroupAction element(Tensor tensor) {
    return new RnGroupElement(tensor);
  }
}
