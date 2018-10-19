// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanWindow.html">BlackmanWindow</a> */
public class BlackmanWindow extends AbstractWindowFunction {
  private static final Scalar _21_50 = RationalScalar.of(21, 50);
  private static final Scalar _2_25 = RationalScalar.of(2, 25);
  // ---
  private static final WindowFunction FUNCTION = new BlackmanWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private BlackmanWindow() {
  }

  @Override
  protected Scalar protected_apply(Scalar x) {
    return StaticHelper.deg2(_21_50, RationalScalar.HALF, _2_25, x);
  }
}
