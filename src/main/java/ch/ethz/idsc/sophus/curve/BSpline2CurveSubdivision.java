// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** quadratic B-spline
 * De Rham
 * Chaikin 1965 */
public class BSpline2CurveSubdivision extends AbstractBSpline2CurveSubdivision {
  public static CurveSubdivision exact(GeodesicInterface geodesicInterface) {
    return new BSpline2CurveSubdivision(geodesicInterface, RealScalar.of(0.25), RealScalar.of(0.75));
  }

  public static CurveSubdivision numeric(GeodesicInterface geodesicInterface) {
    return new BSpline2CurveSubdivision(geodesicInterface, RationalScalar.of(1, 4), RationalScalar.of(3, 4));
  }

  private final Scalar _1_4;
  private final Scalar _3_4;

  private BSpline2CurveSubdivision(GeodesicInterface geodesicInterface, Scalar _1_4, Scalar _3_4) {
    super(geodesicInterface);
    // ---
    this._1_4 = _1_4;
    this._3_4 = _3_4;
  }

  @Override
  protected Tensor refine(Tensor curve, Tensor p, Tensor q) {
    ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(p, q);
    return curve //
        .append(scalarTensorFunction.apply(_1_4)) //
        .append(scalarTensorFunction.apply(_3_4));
  }
}
