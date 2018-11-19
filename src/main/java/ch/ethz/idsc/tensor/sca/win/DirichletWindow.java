// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DirichletWindow.html">DirichletWindow</a> */
public enum DirichletWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? RealScalar.ONE
        : x.zero();
  }
}
