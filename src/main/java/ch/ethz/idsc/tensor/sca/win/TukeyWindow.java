// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TukeyWindow.html">TukeyWindow</a> */
public enum TukeyWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _1_6 = RationalScalar.of(1, 6);
  private static final Scalar _3_PI = RealScalar.of(3 * Math.PI);

  @Override
  public Scalar apply(Scalar x) {
    x = x.abs();
    if (Scalars.lessEquals(x, RationalScalar.HALF)) {
      if (Scalars.lessEquals(x, _1_6))
        return RealScalar.ONE;
      return RationalScalar.HALF.add(RationalScalar.HALF.multiply(Cos.FUNCTION.apply(x.subtract(_1_6).multiply(_3_PI))));
    }
    return x.zero();
  }
}
