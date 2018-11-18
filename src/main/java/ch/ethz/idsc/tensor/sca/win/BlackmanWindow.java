// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanWindow.html">BlackmanWindow</a> */
public class BlackmanWindow extends AbstractWindowFunction {
  private static final Scalar A0 = RationalScalar.of(21, 50);
  private static final Scalar A2 = RationalScalar.of(2, 25);
  // ---
  private static final ScalarUnaryOperator FUNCTION = new BlackmanWindow();

  public static ScalarUnaryOperator function() {
    return FUNCTION;
  }

  // ---
  private BlackmanWindow() {
  }

  @Override // from AbstractWindowFunction
  protected Scalar protected_apply(Scalar x) {
    return StaticHelper.deg2(A0, RationalScalar.HALF, A2, x);
  }
}
