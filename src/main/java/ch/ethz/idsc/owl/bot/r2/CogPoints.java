// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public enum CogPoints {
  ;
  /** the first coordinate is always {1, 0}.
   * the orientation of the points is counter-clockwise.
   * 
   * @param n
   * @param s_hi
   * @param s_lo
   * @return 2 * n x 2 matrix */
  public static Tensor of(int n, Scalar s_hi, Scalar s_lo) {
    int n4 = n * 4;
    Scalar[] radius = new Scalar[] { s_hi, s_hi, s_lo, s_lo };
    int count = 0;
    Tensor polygon = Unprotect.empty(n4);
    for (Tensor u : CirclePoints.of(n4))
      polygon.append(u.multiply(radius[count++ % 4]));
    return polygon;
  }
}
