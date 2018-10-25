// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanHarrisWindow.html">BlackmanHarrisWindow</a> */
public class BlackmanHarrisWindow extends AbstractWindowFunction {
  private static final Scalar A0 = RationalScalar.of(35875, 100000);
  private static final Scalar A1 = RationalScalar.of(48829, 100000);
  private static final Scalar A2 = RationalScalar.of(14128, 100000);
  private static final Scalar A3 = RationalScalar.of(1168, 100000);
  // ---
  private static final WindowFunction FUNCTION = new BlackmanHarrisWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private BlackmanHarrisWindow() {
  }

  @Override
  protected Scalar protected_apply(Scalar x) {
    return StaticHelper.deg3(A0, A1, A2, A3, x);
  }

  @Override
  public boolean isContinuous() {
    return true;
  }
}
