// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** triangular function max(0, 1 - 2*|x|)
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BartlettWindow.html">BartlettWindow</a> */
public enum BartlettWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? RealScalar.ONE.subtract(x.add(x).abs())
        : x.zero();
  }
}
