// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.SplitInterface;
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
  protected final SplitInterface splitInterface;

  public BSpline3CurveSubdivision(SplitInterface splitInterface) {
    this.splitInterface = splitInterface;
  }

  /** @param p
   * @param q
   * @return point between p and q */
  @Override
  public final Tensor midpoint(Tensor p, Tensor q) {
    return splitInterface.split(p, q, RationalScalar.HALF);
  }

  /** @param p
   * @param q
   * @param r
   * @return reposition of point q */
  @Override
  protected final Tensor center(Tensor p, Tensor q, Tensor r) {
    return midpoint( //
        splitInterface.split(p, q, _3_4), //
        splitInterface.split(q, r, _1_4));
  }
}
