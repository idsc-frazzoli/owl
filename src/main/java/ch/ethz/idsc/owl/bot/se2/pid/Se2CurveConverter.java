// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum Se2CurveConverter implements Se2UnitConverter {
  INSTANCE;
  /** @param unitless traj
   * @return traj with unit {x[m], y[m], phi[-]} */
  @Override
  public Tensor toSI(Tensor traj) {
    Tensor trajMeter = Tensors.empty();
    traj.forEach(i -> trajMeter.append(Tensors.of( //
        Quantity.of(i.Get(0), "m"), //
        Quantity.of(i.Get(1), "m"), //
        Quantity.of(i.Get(2), ""))));
    return trajMeter;
  }
}
