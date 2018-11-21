// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ParzenWindow.html">ParzenWindow</a> */
public enum ParzenWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final ScalarUnaryOperator S1 = Series.of(Tensors.vector(1, 0, -24, 48));
  private static final ScalarUnaryOperator S2 = Series.of(Tensors.vector(2, -12, 24, -16));

  @Override
  public Scalar apply(Scalar x) {
    x = x.abs();
    if (Scalars.lessEquals(x, RationalScalar.HALF))
      return Scalars.lessEquals(x, _1_4) //
          ? S1.apply(x)
          : S2.apply(x);
    return x.zero();
  }
}
