// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

/** reference implementation of {@link WindowFunction} interface */
public abstract class AbstractWindowFunction implements WindowFunction {
  static final Clip SEMI = Clip.function(RationalScalar.HALF.negate(), RationalScalar.HALF);

  @Override
  public final Scalar apply(Scalar x) {
    // TODO mathematica evaluates to zero outside of interval
    SEMI.requireInside(x);
    return protected_apply(x);
  }

  @Override
  public final boolean isZero() {
    return Chop._10.allZero(protected_apply(RationalScalar.HALF));
  }

  protected abstract Scalar protected_apply(Scalar x);
}
