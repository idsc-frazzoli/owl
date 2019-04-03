// code by mcp
package ch.ethz.idsc.owl.controller.pid;

import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ public enum RnCurveHelper {
  ;
  /** @param curve
   * @param pose
   * @return position of the closest point on the curve to the current pose */
  public static int closest(Tensor curve, Tensor pose) {
    return ArgMin.of(Tensor.of(curve.stream().map(curvePoint -> Norm._2.between(curvePoint, pose))));
  }

  /** @param curve
   * @return appends angle between two following points on the curve */
  public static Tensor addAngleToCurve(Tensor curve) {
    for (int index = 0; index < curve.length(); index++) { // TODO MCP Write this better (stream)
      int nextIndex = index+1;
      if (index == curve.length()-1)
        nextIndex = 0;
      curve.get(index).append(ArcTan2D.of(curve.get(nextIndex).subtract(curve.get(index))));
    }
    return curve;
  }

  /** @param optionalCurve
   * @return if enough elements in curve */
  public static boolean bigEnough(Tensor optionalCurve) {
    return optionalCurve.length() > 1; // TODO MCP Write this better
  }
}