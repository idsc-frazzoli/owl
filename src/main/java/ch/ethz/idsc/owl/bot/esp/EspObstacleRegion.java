// code by jph
package ch.ethz.idsc.owl.bot.esp;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum EspObstacleRegion implements Region<Tensor> {
  INSTANCE;

  @Override
  public boolean isMember(Tensor tensor) {
    return ScalarQ.of(tensor);
  }
}
