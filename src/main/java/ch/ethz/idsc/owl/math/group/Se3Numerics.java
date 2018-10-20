// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sinc;

/** the computation of the exponential and logarithm functions for SE3
 * require the evaluation of taylor series to prevent numerical
 * instability */
/* package */ class Se3Numerics {
  private static final ScalarUnaryOperator SERIES1 = Series.of(N.DOUBLE.of(Tensors.fromString( //
      "{1/2, 0, -1/24, 0, 1/720, 0, -1/40320, 0, 1/3628800, 0, -1/479001600, 0, 1/87178291200, 0, -1/20922789888000}")));
  private static final ScalarUnaryOperator SERIES2 = Series.of(N.DOUBLE.of(Tensors.fromString( //
      "{1/6, 0, -1/120, 0, 1/5040, 0, -1/362880, 0, 1/39916800, 0, -1/6227020800, 0, 1/1307674368000, 0, -1/355687428096000}")));
  private static final ScalarUnaryOperator SERIES3 = Series.of(N.DOUBLE.of(Tensors.fromString( //
      "{1/12, 0, 1/720, 0, 1/30240, 0, 1/1209600, 0, 1/47900160, 0, 691/1307674368000, 0, 1/74724249600, 0, 3617/10670622842880000}")));
  final boolean series;
  final Scalar A;
  final Scalar B;
  final Scalar C;
  /** D is only used in log function */
  final Scalar D;

  public Se3Numerics(Scalar theta) {
    A = Sinc.FUNCTION.apply(theta);
    Scalar theta2 = theta.multiply(theta);
    series = Chop._04.allZero(theta2.abs());
    if (series) {
      B = SERIES1.apply(theta);
      C = SERIES2.apply(theta);
      D = SERIES3.apply(theta);
    } else {
      B = RealScalar.ONE.subtract(Cos.FUNCTION.apply(theta)).divide(theta2);
      C = RealScalar.ONE.subtract(A).divide(theta2);
      // D is difficult to evaluate
      D = RealScalar.ONE.subtract(A.divide(B.add(B))).divide(theta2);
    }
  }

  Tensor vector() {
    return Tensors.of(Boole.of(series), A, B, C, D);
  }
}
