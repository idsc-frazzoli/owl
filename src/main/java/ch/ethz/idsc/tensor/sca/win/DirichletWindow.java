// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DirichletWindow.html">DirichletWindow</a> */
public class DirichletWindow extends AbstractWindowFunction {
  private static final WindowFunction FUNCTION = new DirichletWindow();

  public static WindowFunction function() {
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
