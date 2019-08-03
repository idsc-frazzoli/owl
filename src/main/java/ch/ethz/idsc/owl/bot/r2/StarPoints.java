// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public enum StarPoints {
  ;
  /** the orientation of the points is counter-clockwise.
   * 
   * @param n
   * @return (2 * n) x 2 matrix */
  public static Tensor of(int n, Scalar s_hi, Scalar s_lo) {
    int n2 = n * 2;
    Scalar[] radius = new Scalar[] { s_hi, s_lo };
    int count = 0;
    Tensor polygon = Tensors.reserve(n2);
    for (Tensor u : CirclePoints.of(n2))
      polygon.append(u.multiply(radius[count++ % 2]));
    return polygon;
  }
}
