// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanHarrisWindow.html">BlackmanHarrisWindow</a> */
public enum BlackmanHarrisWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar A0 = RationalScalar.of(35875, 100000);
  private static final Scalar A1 = RationalScalar.of(48829, 100000);
  private static final Scalar A2 = RationalScalar.of(14128, 100000);
  private static final Scalar A3 = RationalScalar.of(1168, 100000);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg3(A0, A1, A2, A3, x)
        : x.zero();
  }
}
