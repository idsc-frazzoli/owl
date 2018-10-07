// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HammingWindow.html">HammingWindow</a> */
public enum HammingWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _25_46 = RationalScalar.of(25, 46);
  private static final Scalar _21_46 = RationalScalar.of(21, 46);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.deg1(_25_46, _21_46, x);
  }
}
