// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;

/* package */ enum Se2CurveHelper {
  ;
  /** @param curve
   * @param pose {x, y, heading}
   * @return */
  public static int closest(Tensor curve, Tensor pose) {
    return ArgMin.of(Tensor.of(curve.stream() //
        .map(curvePoint -> Se2ParametricDistance.INSTANCE.distance(curvePoint, pose))));
  }
}
