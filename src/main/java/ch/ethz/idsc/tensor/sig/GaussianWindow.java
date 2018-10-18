// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/GaussianWindow.html">GaussianWindow</a> */
public enum GaussianWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _50_9 = RationalScalar.of(-50, 9);

  @Override
  public Scalar apply(Scalar x) {
    StaticHelper.SEMI.requireInside(x);
    return Exp.FUNCTION.apply(Times.of(_50_9, x, x));
  }
}
