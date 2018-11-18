// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HammingWindow.html">HammingWindow</a> */
public class HammingWindow extends AbstractWindowFunction {
  private static final Scalar A0 = RationalScalar.of(25, 46);
  private static final Scalar A1 = RationalScalar.of(21, 46);
  // ---
  private static final ScalarUnaryOperator FUNCTION = new HammingWindow();

  public static ScalarUnaryOperator function() {
    return FUNCTION;
  }

  // ---
  private HammingWindow() {
  }

  @Override // from AbstractWindowFunction
  public Scalar protected_apply(Scalar x) {
    return StaticHelper.deg1(A0, A1, x);
  }
}
