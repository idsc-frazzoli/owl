// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DirichletWindow.html">DirichletWindow</a> */
public class DirichletWindow extends AbstractWindowFunction {
  private static final ScalarUnaryOperator FUNCTION = new DirichletWindow();

  public static ScalarUnaryOperator function() {
    return FUNCTION;
  }

  // ---
  private DirichletWindow() {
  }

  @Override
  protected Scalar protected_apply(Scalar x) {
    return RealScalar.ONE;
  }
}
