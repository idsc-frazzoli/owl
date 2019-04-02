//code by mcp
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

public enum PIDCurveHelper {
  ;
  /** @param curve
   * @param pose
   * @return position of the closest point on the curve to the current pose */
  public static int closest(Tensor curve, Tensor pose) {
    return ArgMin.of(Tensor.of(curve.stream().map(curvePoint -> Norm._2.between(curvePoint, pose))));
  }

  /** @param curve
   * @param point
   * @return angle between two following points of the closest point on the curve to the current pose */
  public static Scalar trajAngle(Tensor curve, Tensor point) {
    int index = closest(curve, point);
    int nextIndex = index + 1;
    if (nextIndex > curve.length()) // TODO MCP Write this better
      nextIndex = 0;
    return ArcTan2D.of(curve.get(nextIndex).subtract(curve.get(index)));
  }

  /** @param optionalCurve
   * @return if enough elements in curve */
  public static boolean bigEnough(Tensor optionalCurve) {
    return optionalCurve.length() > 1; // TODO MCP Write this better
  }
}