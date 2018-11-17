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
public class TukeyWindow extends AbstractWindowFunction {
  private static final Scalar _1_6 = RationalScalar.of(1, 6);
  private static final Scalar _3_PI = RealScalar.of(3 * Math.PI);
  // ---
  private static final ScalarUnaryOperator FUNCTION = new TukeyWindow();

  public static ScalarUnaryOperator function() {
    return FUNCTION;
  }

  // ---
  private TukeyWindow() {
  }

  @Override
  public Scalar protected_apply(Scalar x) {
    x = x.abs();
    if (Scalars.lessEquals(x, _1_6))
      return RealScalar.ONE;
    return RationalScalar.HALF.add(RationalScalar.HALF.multiply(Cos.FUNCTION.apply(x.subtract(_1_6).multiply(_3_PI))));
  }
}
