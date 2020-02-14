// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see CirclePoints */
public enum EllipsePoints {
  ;
  /** @param n
   * @param scale vector of length 2
   * @return */
  public static Tensor of(int n, Tensor scale) {
    return Tensor.of(CirclePoints.of(n).stream().map(row -> row.pmul(scale)));
  }

  /** @param n
   * @param width
   * @param height
   * @return */
  public static Tensor of(int n, Scalar width, Scalar height) {
    return of(n, Tensors.of(width, height));
  }
}
