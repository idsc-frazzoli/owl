// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.BSplineInterpolation;

/** computation of control points that result in a limit curve
 * that interpolates given target points.
 * 
 * Hint: when target coordinates are specified in exact precision,
 * the iteration may involve computing fractions consisting of large
 * integers. Therefore, it is recommended to provide target points
 * in numeric precision.
 * 
 * @see BSplineInterpolation */
public final class LieGroupBSplineInterpolation extends AbstractBSplineInterpolation {
  private final LieGroup lieGroup;

  /** @param lieGroup
   * @param splitInterface corresponding to lie group
   * @param degree of underlying b-spline */
  public LieGroupBSplineInterpolation(LieGroup lieGroup, SplitInterface splitInterface, int degree, Tensor target) {
    super(splitInterface, degree, target);
    this.lieGroup = lieGroup;
  }

  @Override // from GeodesicBSplineInterpolation
  protected Tensor move(Tensor p, Tensor e, Tensor t) {
    return lieGroup.element(p).combine(lieGroup.element(e).inverse().combine(t));
  }
}
