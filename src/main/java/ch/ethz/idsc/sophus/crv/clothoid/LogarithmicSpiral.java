// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Exp;

/** https://en.wikipedia.org/wiki/Logarithmic_spiral */
public class LogarithmicSpiral implements ScalarTensorFunction {
  private final Scalar a;
  private final Scalar b;

  /** @param a
   * @param b for instance 0.1759 */
  public LogarithmicSpiral(Scalar a, Scalar b) {
    this.a = a;
    this.b = b;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar theta) {
    Scalar radius = a.multiply(Exp.FUNCTION.apply(b.multiply(theta)));
    return AngleVector.of(theta).multiply(radius);
  }
}
