// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** quadratic B-spline */
public class LaneRiesenfeld2CurveSubdivision extends AbstractBSpline2CurveSubdivision {
  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return new LaneRiesenfeld2CurveSubdivision(geodesicInterface, RationalScalar.HALF);
  }

  public static CurveSubdivision numeric(GeodesicInterface geodesicInterface) {
    return new LaneRiesenfeld2CurveSubdivision(geodesicInterface, RealScalar.of(0.5));
  }

  // ---
  private final Scalar half;

  private LaneRiesenfeld2CurveSubdivision(GeodesicInterface geodesicInterface, Scalar half) {
    super(geodesicInterface);
    // ---
    this.half = half;
  }

  @Override // from BSpline2CurveSubdivision
  protected Tensor refine(Tensor curve, Tensor p, Tensor q) {
    Tensor pq = center(p, q);
    return curve //
        .append(center(p, pq)) //
        .append(center(pq, q));
  }

  private final Tensor center(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, half);
  }
}
