// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum HuarongGoalRegion implements Region<Tensor> {
  INSTANCE;
  // ---
  @Override
  public boolean isMember(Tensor x) {
    return x.get(0).equals(Tensors.vector(0, 4, 2));
  }
}
