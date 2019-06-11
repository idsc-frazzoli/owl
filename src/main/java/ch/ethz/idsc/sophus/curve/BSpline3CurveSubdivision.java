// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** cubic B-spline
 * 
 * Dyn/Sharon 2014 p.16 show that the scheme has a contractivity factor of mu = 1/2 */
public class BSpline3CurveSubdivision extends RefiningBSpline3CurveSubdivision {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);
  // ---
  protected final GeodesicInterface geodesicInterface;

  public BSpline3CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  /** @param p
   * @param q
   * @return point between p and q */
  @Override
  protected final Tensor center(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, RationalScalar.HALF);
  }

  /** @param p
   * @param q
   * @param r
   * @return reposition of point q */
  @Override
  protected final Tensor center(Tensor p, Tensor q, Tensor r) {
    return center( //
        geodesicInterface.split(p, q, _3_4), //
        geodesicInterface.split(q, r, _1_4));
  }
}
