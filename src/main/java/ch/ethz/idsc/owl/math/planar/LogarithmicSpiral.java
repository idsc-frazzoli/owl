// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** https://en.wikipedia.org/wiki/Logarithmic_spiral */
public class LogarithmicSpiral implements ScalarUnaryOperator {
  private final Scalar a;
  private final Scalar b;

  /** @param a
   * @param b for instance 0.1759 */
  public LogarithmicSpiral(Scalar a, Scalar b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public Scalar apply(Scalar theta) {
    return a.multiply(Exp.FUNCTION.apply(b.multiply(theta)));
  }
}
