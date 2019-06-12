// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;

/** linear B-spline
 * 
 * the scheme interpolates the control points
 * 
 * Dyn/Sharon 2014 p.14 show that the contractivity factor is mu = 1/2 */
public class BSpline1CurveSubdivision extends AbstractBSpline1CurveSubdivision {
  protected final SplitInterface splitInterface;

  public BSpline1CurveSubdivision(SplitInterface splitInterface) {
    this.splitInterface = splitInterface;
  }

  /** @param p
   * @param q
   * @return point between p and q */
  @Override
  public final Tensor midpoint(Tensor p, Tensor q) {
    return splitInterface.split(p, q, RationalScalar.HALF);
  }
}
