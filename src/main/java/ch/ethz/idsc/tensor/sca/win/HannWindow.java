// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/HannWindow.html">HannWindow</a> */
public class HannWindow extends AbstractWindowFunction {
  private static final Scalar _1_3 = RationalScalar.of(1, 3);
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _1_6 = RationalScalar.of(1, 6);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);
  // ---
  private static final WindowFunction FUNCTION = new HannWindow();

  public static WindowFunction function() {
    return FUNCTION;
  }

  // ---
  private HannWindow() {
  }

  @Override
  public Scalar protected_apply(Scalar x) {
    x = x.abs();
    if (ExactScalarQ.of(x)) {
      if (x.equals(RealScalar.ZERO))
        return RealScalar.ONE;
      if (x.equals(_1_3))
        return _1_4;
      if (x.equals(_1_4))
        return RationalScalar.HALF;
      if (x.equals(_1_6))
        return _3_4;
    }
    return StaticHelper.deg1(RationalScalar.HALF, RationalScalar.HALF, x);
  }
}
