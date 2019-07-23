// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

/** clothoid is tangent at start and end points */
// TODO JPH implementation seems to have issues
/* package */ class ClothoidTerminalRatios2 implements Serializable {
  public static final CurveSubdivision CURVE_SUBDIVISION = //
      new LaneRiesenfeldCurveSubdivision(Clothoid1.INSTANCE, 1);
  private static final TensorUnaryOperator HEAD = //
      value -> CURVE_SUBDIVISION.string(Tensor.of(value.stream().limit(2)));
  private static final TensorUnaryOperator TAIL = //
      value -> CURVE_SUBDIVISION.string(Tensor.of(value.stream().skip(value.length() - 2)));
  private static final int ITERATIONS = 6;

  public static ClothoidTerminalRatios of(Tensor p, Tensor q) {
    return new ClothoidTerminalRatios(head(p, q), tail(p, q));
  }

  /** @param p of the form {p_x, p_y, p_heading}
   * @param q of the form {q_x, q_y, q_heading}
   * @return */
  public static Scalar head(Tensor p, Tensor q) {
    Tensor tensor = Nest.of(HEAD, Unprotect.byRef(p, q), ITERATIONS);
    return new ClothoidCurvature(tensor.get(0), tensor.get(1)).head();
  }

  /** @param p of the form {p_x, p_y, p_heading}
   * @param q of the form {q_x, q_y, q_heading}
   * @return */
  public static Scalar tail(Tensor p, Tensor q) {
    Tensor tensor = Nest.of(TAIL, Unprotect.byRef(p, q), ITERATIONS);
    return new ClothoidCurvature(tensor.get(0), tensor.get(1)).tail();
  }
}
