// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanHarrisWindow.html">BlackmanHarrisWindow</a> */
public class BlackmanNuttallWindow extends AbstractWindowFunction {
  private static final Scalar A0 = RationalScalar.of(3635819, 10000000);
  private static final Scalar A1 = RationalScalar.of(4891775, 10000000);
  private static final Scalar A2 = RationalScalar.of(1365995, 10000000);
  private static final Scalar A3 = RationalScalar.of(106411, 10000000);
  // ---
  private static final WindowFunction FUNCTION = new BlackmanNuttallWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private BlackmanNuttallWindow() {
  }

  @Override
  protected Scalar protected_apply(Scalar x) {
    return StaticHelper.deg3(A0, A1, A2, A3, x);
  }
}
