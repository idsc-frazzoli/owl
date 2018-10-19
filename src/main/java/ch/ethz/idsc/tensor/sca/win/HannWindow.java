// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Rationalize;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HannWindow.html">HannWindow</a> */
public class HannWindow extends AbstractWindowFunction {
  private static final ScalarUnaryOperator RATIONALIZE = Rationalize.withDenominatorLessEquals(100);
  private static final WindowFunction FUNCTION = new HannWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private HannWindow() {
  }

  @Override
  public Scalar protected_apply(Scalar x) {
    Scalar scalar = StaticHelper.deg1(RationalScalar.HALF, RationalScalar.HALF, x);
    // TODO this is not reasonable
    Scalar apply = RATIONALIZE.apply(scalar);
    return Chop._08.close(scalar, apply) //
        ? apply
        : scalar;
  }
}
