// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** non-public base class for the implementation of a window function
 * that has support in the interval [-1/2, +1/2] and evaluates to zero
 * outside the interval. */
/* package */ abstract class AbstractWindowFunction implements ScalarUnaryOperator {
  private static final Clip SEMI = Clip.function( //
      RationalScalar.HALF.negate(), //
      RationalScalar.HALF);

  @Override
  public final Scalar apply(Scalar x) {
    return SEMI.isInside(x) //
        ? protected_apply(x)
        : x.zero();
  }

  /** @param x in the interval [-1/2, 1/2]
   * @return window function evaluated at given parameter x */
  protected abstract Scalar protected_apply(Scalar x);
}
