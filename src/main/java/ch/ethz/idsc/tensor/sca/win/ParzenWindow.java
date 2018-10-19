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
public class ParzenWindow extends AbstractWindowFunction {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final ScalarUnaryOperator S1 = Series.of(Tensors.vector(1, 0, -24, 48));
  private static final ScalarUnaryOperator S2 = Series.of(Tensors.vector(2, -12, 24, -16));
  // ---
  private static final WindowFunction FUNCTION = new ParzenWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private ParzenWindow() {
  }

  @Override
  public Scalar protected_apply(Scalar x) {
    x = x.abs();
    return Scalars.lessEquals(x, _1_4) //
        ? S1.apply(x)
        : S2.apply(x);
  }
}
