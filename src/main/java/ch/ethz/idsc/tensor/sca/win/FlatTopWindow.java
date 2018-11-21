// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** the flat-top window function also evaluates to negative values
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FlatTopWindow.html">FlatTopWindow</a> */
public enum FlatTopWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar A0 = RationalScalar.of(215578947, 1000000000);
  private static final Scalar A1 = RationalScalar.of(416631580, 1000000000);
  private static final Scalar A2 = RationalScalar.of(277263158, 1000000000);
  private static final Scalar A3 = RationalScalar.of(83578947, 1000000000);
  private static final Scalar A4 = RationalScalar.of(6947368, 1000000000);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg4(A0, A1, A2, A3, A4, x)
        : x.zero();
  }
}
