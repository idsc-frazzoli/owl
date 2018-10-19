// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DirichletWindow.html">DirichletWindow</a> */
public class DirichletWindow extends AbstractWindowFunction {
  @Override
  protected Scalar protected_apply(Scalar x) {
    return RealScalar.ONE;
  }
}
