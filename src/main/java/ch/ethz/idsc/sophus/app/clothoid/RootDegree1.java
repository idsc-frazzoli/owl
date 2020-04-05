// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import ch.ethz.idsc.tensor.Scalar;

/* package */ enum RootDegree1 {
  ;
  /** @param x0
   * @param x1
   * @param y0
   * @param y1
   * @return (x0 y1 - x1 y0) / (y1 - y0) */
  public static Scalar of(Scalar x0, Scalar x1, Scalar y0, Scalar y1) {
    return x0.multiply(y1).subtract(x1.multiply(y0)).divide(y1.subtract(y0));
  }
}
