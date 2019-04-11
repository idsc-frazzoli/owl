// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum Se2CurveConverter {
  ;
  /** @param traj
   * @return */
  public static Tensor toMeter(Tensor traj) {
    Tensor trajMeter = Tensors.empty();
    for (int i = 0; i < traj.length(); i++) {
      Tensor xyPoint = Tensors.of( //
          Quantity.of(traj.get(i).Get(0), "m"), //
          Quantity.of(traj.get(i).Get(1), "m"), //
          Quantity.of(traj.get(i).Get(2), ""));
      trajMeter.append(xyPoint);
    }
    return trajMeter;
  }
}
