// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.LieGroupAction;
import ch.ethz.idsc.tensor.Tensor;

public interface LieGroup {
  LieGroupAction element(Tensor tensor);
}
