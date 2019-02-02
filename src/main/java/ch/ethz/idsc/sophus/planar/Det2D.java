// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;

public enum Det2D {
  ;
  /** .
   * @param p vector of length 2 with entries {px, py}
   * @param q vector of length 2 with entries {qx, qy}
   * @return px * qy - py * qx
   * @throws Exception if p or q is not a vector of length 2 */
  public static Scalar of(Tensor p, Tensor q) {
    return Cross.of(p).dot(q).Get();
  }
}
