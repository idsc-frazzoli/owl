// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Tensor;

public enum RnCurveHelper {
  ;
  /** @param curve
   * @return appends angle between two following points on the curve */
  // TODO JPH/MCP create new curve (instead of modifying given one)
  public static Tensor addAngleToCurve(Tensor curve) {
    for (int index = 0; index < curve.length(); ++index) { // TODO MCP Write this better (stream)
      int nextIndex = index + 1;
      if (index == curve.length() - 1)
        nextIndex = 0;
      curve.get(index).append(ArcTan2D.of(curve.get(nextIndex).subtract(curve.get(index))));
    }
    return curve;
  }

  /** @param optionalCurve
   * @return if enough elements in curve */
  public static boolean bigEnough(Tensor optionalCurve) {
    return 1 < optionalCurve.length(); // TODO MCP Write this better
  }
}