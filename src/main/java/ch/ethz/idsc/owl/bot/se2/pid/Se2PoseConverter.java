// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum Se2PoseConverter implements Se2UnitConverter {
  INSTANCE;
  /** @param unitless pose
   * @return pose with unit {x[m], y[m], phi[-]} */
  @Override
  public Tensor toSI(Tensor pose) {
    return Tensors.of( //
        Quantity.of(pose.Get(0), "m"), //
        Quantity.of(pose.Get(1), "m"), //
        Quantity.of(pose.Get(2), ""));
  }
}