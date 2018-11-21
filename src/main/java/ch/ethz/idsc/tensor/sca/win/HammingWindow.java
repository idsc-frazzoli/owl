// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HammingWindow.html">HammingWindow</a> */
public enum HammingWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar A0 = RationalScalar.of(25, 46);
  private static final Scalar A1 = RationalScalar.of(21, 46);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg1(A0, A1, x)
        : x.zero();
  }
}
