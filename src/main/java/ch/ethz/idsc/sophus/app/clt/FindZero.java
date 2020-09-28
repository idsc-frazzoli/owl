// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.io.Serializable;
import java.util.function.Predicate;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class FindZero implements Serializable {
  private static final int MAX_ITERATIONS = 128;
  // ---
  private final ScalarUnaryOperator function;
  private final Predicate<Scalar> predicate;
  private final Chop chop;

  /** @param function
   * @param predicate for instance Sign::isPositive
   * @param chop */
  public FindZero(ScalarUnaryOperator function, Predicate<Scalar> predicate, Chop chop) {
    this.function = function;
    this.predicate = predicate;
    this.chop = chop;
  }

  public Scalar between(Scalar x0, Scalar x1) {
    return between(x0, x1, function.apply(x0), function.apply(x1));
  }

  public Scalar between(Scalar x0, Scalar x1, Scalar y0, Scalar y1) {
    Scalar xn = x0;
    for (int index = 0; index < MAX_ITERATIONS; ++index) {
      xn = linear(x0, x1, y0, y1);
      Scalar yn = function.apply(xn);
      if (chop.isZero(yn)) {
        System.out.println(index);
        return xn;
      }
      boolean isLo = predicate.test(yn);
      if (isLo) {
        x0 = xn;
        y0 = yn;
      } else {
        x1 = xn;
        y1 = yn;
      }
    }
    throw new RuntimeException();
  }

  /** @param x0
   * @param x1
   * @param y0
   * @param y1
   * @return (x0 y1 - x1 y0) / (y1 - y0) */
  /* package */ static Scalar linear(Scalar x0, Scalar x1, Scalar y0, Scalar y1) {
    return x0.multiply(y1).subtract(x1.multiply(y0)).divide(y1.subtract(y0));
  }
}
