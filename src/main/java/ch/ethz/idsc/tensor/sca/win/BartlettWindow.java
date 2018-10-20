// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BartlettWindow.html">BartlettWindow</a> */
public class BartlettWindow extends AbstractWindowFunction {
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  private static final WindowFunction FUNCTION = new BartlettWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private BartlettWindow() {
  }

  @Override
  protected Scalar protected_apply(Scalar x) {
    return RealScalar.ONE.subtract(x.abs().multiply(TWO));
  }
}
