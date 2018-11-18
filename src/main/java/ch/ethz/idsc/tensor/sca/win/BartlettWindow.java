// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BartlettWindow.html">BartlettWindow</a> */
public class BartlettWindow extends AbstractWindowFunction {
  private static final ScalarUnaryOperator FUNCTION = new BartlettWindow();

  public static ScalarUnaryOperator function() {
    return FUNCTION;
  }

  // ---
  private BartlettWindow() {
  }

  @Override // from AbstractWindowFunction
  protected Scalar protected_apply(Scalar x) {
    return RealScalar.ONE.subtract(x.add(x).abs());
  }
}
