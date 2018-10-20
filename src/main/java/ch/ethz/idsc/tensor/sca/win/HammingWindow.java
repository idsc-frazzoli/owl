// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HammingWindow.html">HammingWindow</a> */
public class HammingWindow extends AbstractWindowFunction {
  private static final Scalar _25_46 = RationalScalar.of(25, 46);
  private static final Scalar _21_46 = RationalScalar.of(21, 46);
  // ---
  private static final WindowFunction FUNCTION = new HammingWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private HammingWindow() {
  }

  @Override
  public Scalar protected_apply(Scalar x) {
    return StaticHelper.deg1(_25_46, _21_46, x);
  }
}
