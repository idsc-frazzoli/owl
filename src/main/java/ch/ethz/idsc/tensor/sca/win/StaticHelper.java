// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Cos;

/** helper functions to evaluate window functions */
/* package */ enum StaticHelper {
  ;
  static final Clip SEMI = Clip.function( //
      RationalScalar.HALF.negate(), //
      RationalScalar.HALF);
  // ---
  private static final Scalar _2_PI = RealScalar.of(2 * Math.PI);
  private static final Scalar _4_PI = RealScalar.of(4 * Math.PI);
  private static final Scalar _6_PI = RealScalar.of(6 * Math.PI);
  private static final Scalar _8_PI = RealScalar.of(8 * Math.PI);

  static Scalar deg1(Scalar a0, Scalar a1, Scalar x) {
    return a0.add(a1.multiply(Cos.FUNCTION.apply(x.multiply(_2_PI))));
  }

  static Scalar deg2(Scalar a0, Scalar a1, Scalar a2, Scalar x) {
    return deg1(a0, a1, x).add(a2.multiply(Cos.FUNCTION.apply(x.multiply(_4_PI))));
  }

  /** used in Nuttall as well as Blackman-Harris window
   * 
   * a0 + a1 Cos[2pi x] + a2 Cos[4pi x] + a3 Cos[6pi x]
   * 
   * @param a0
   * @param a1
   * @param a2
   * @param a3
   * @param x
   * @return */
  static Scalar deg3(Scalar a0, Scalar a1, Scalar a2, Scalar a3, Scalar x) {
    return deg2(a0, a1, a2, x).add(a3.multiply(Cos.FUNCTION.apply(x.multiply(_6_PI))));
  }

  static Scalar deg4(Scalar a0, Scalar a1, Scalar a2, Scalar a3, Scalar a4, Scalar x) {
    return deg3(a0, a1, a2, a3, x).add(a4.multiply(Cos.FUNCTION.apply(x.multiply(_8_PI))));
  }
}
