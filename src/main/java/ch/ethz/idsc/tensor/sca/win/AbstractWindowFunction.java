// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

/** reference implementation of {@link WindowFunction} interface */
public abstract class AbstractWindowFunction implements WindowFunction {
  private static final Clip SEMI = Clip.function(RationalScalar.HALF.negate(), RationalScalar.HALF);

  @Override
  public final Scalar apply(Scalar x) {
    return SEMI.isInside(x) //
        ? protected_apply(x)
        : RealScalar.ZERO;
  }

  @Override // from WindowFunction
  public boolean isContinuous() {
    return Chop._10.allZero(protected_apply(RationalScalar.HALF));
  }

  /** @param x in the interval [-1/2, 1/2]
   * @return window function evaluated at given parameter x */
  protected abstract Scalar protected_apply(Scalar x);
}
