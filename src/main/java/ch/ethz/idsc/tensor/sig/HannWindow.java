// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HannWindow.html">HannWindow</a> */
public enum HannWindow implements ScalarUnaryOperator {
  FUNCTION;
  private static final ScalarUnaryOperator RATIONALIZE = Rationalize.withDenominatorLessEquals(100);

  // ---
  @Override
  public Scalar apply(Scalar x) {
    Scalar scalar = StaticHelper.deg1(RationalScalar.HALF, RationalScalar.HALF, x);
    Scalar apply = RATIONALIZE.apply(scalar);
    return Chop._08.close(scalar, apply) //
        ? apply
        : scalar;
  }
}
