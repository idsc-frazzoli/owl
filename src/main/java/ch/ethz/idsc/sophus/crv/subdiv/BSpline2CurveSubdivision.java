// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.ParametricCurve;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** quadratic B-spline
 * De Rham
 * Chaikin 1965
 * 
 * the same connecting curve is evaluated at 2 different parameter values: 1/4 and 3/4 */
public class BSpline2CurveSubdivision extends AbstractBSpline2CurveSubdivision {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);
  // ---
  protected final ParametricCurve parametricCurve;

  /** @param parametricCurve non-null */
  public BSpline2CurveSubdivision(ParametricCurve parametricCurve) {
    this.parametricCurve = Objects.requireNonNull(parametricCurve);
  }

  @Override // from AbstractBSpline2CurveSubdivision
  protected Tensor refine(Tensor curve, Tensor p, Tensor q) {
    ScalarTensorFunction scalarTensorFunction = parametricCurve.curve(p, q);
    return curve //
        .append(scalarTensorFunction.apply(_1_4)) //
        .append(scalarTensorFunction.apply(_3_4));
  }
}
