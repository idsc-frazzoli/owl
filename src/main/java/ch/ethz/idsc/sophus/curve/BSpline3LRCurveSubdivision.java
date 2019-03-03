// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;

/** cubic B-spline
 * 
 * Dyn/Sharon 2014 p.16 show that the scheme has a contractivity factor of mu = 1/2 */
public class BSpline3LRCurveSubdivision extends BSpline3CurveSubdivision {
  public BSpline3LRCurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  // TODO JPH reuse the center computation of the midpoints
  @Override
  protected final Tensor center(Tensor p, Tensor q, Tensor r) {
    Tensor pq = center(center(p, q), q);
    Tensor qr = center(q, center(q, r));
    return center(pq, qr);
  }
}
