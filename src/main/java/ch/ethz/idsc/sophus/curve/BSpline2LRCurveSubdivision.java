// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;

/** quadratic B-spline */
public class BSpline2LRCurveSubdivision extends BSpline2CurveSubdivision {
  public BSpline2LRCurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override
  Tensor refine(Tensor curve, Tensor p, Tensor q) {
    Tensor pq = center(p, q);
    return curve //
        .append(center(p, pq)) //
        .append(center(pq, q));
  }

  protected final Tensor center(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, RationalScalar.HALF);
  }
}
