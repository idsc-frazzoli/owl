// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** Reference: U. Reif slide 9/32 */
/* package */ enum ClothoidApproximation {
  ;
  private static final Scalar _1_68 = RealScalar.of(1 / 68.0);
  private static final Scalar _1_46 = RealScalar.of(1 / 46.0);
  private static final Scalar _1_4 = RealScalar.of(0.25);

  /** @param b0
   * @param b1
   * @return tilde f(b0, b1) */
  static Scalar f(Scalar b0, Scalar b1) {
    Scalar f1 = b0.multiply(b0).add(b1.multiply(b1)).multiply(_1_68);
    Scalar f2 = b0.multiply(b1).multiply(_1_46);
    Scalar f3 = _1_4;
    return b0.add(b1).multiply(f1.subtract(f2).subtract(f3));
  }
}
