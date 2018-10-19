// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Cos;

/** helper functions to evaluate window functions */
/* package */ enum StaticHelper {
  ;
  private static final Scalar _2_PI = RealScalar.of(2 * Math.PI);
  private static final Scalar _4_PI = RealScalar.of(4 * Math.PI);
  private static final Scalar _6_PI = RealScalar.of(6 * Math.PI);

  static Scalar deg1(Scalar a0, Scalar a1, Scalar x) {
    return a0.add(a1.multiply(Cos.FUNCTION.apply(x.multiply(_2_PI))));
  }

  static Scalar deg2(Scalar a0, Scalar a1, Scalar a2, Scalar x) {
    return deg1(a0, a1, x).add(a2.multiply(Cos.FUNCTION.apply(x.multiply(_4_PI))));
  }

  static Scalar deg3(Scalar a0, Scalar a1, Scalar a2, Scalar a3, Scalar x) {
    return deg2(a0, a1, a2, x).add(a3.multiply(Cos.FUNCTION.apply(x.multiply(_6_PI))));
  }
}
