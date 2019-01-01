// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
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
public final class LieGroupBSplineInterpolation extends GeodesicBSplineInterpolation {
  private final LieGroup lieGroup;

  /** @param lieGroup
   * @param geodesicInterface corresponding to lie group
   * @param degree of underlying b-spline */
  public LieGroupBSplineInterpolation(LieGroup lieGroup, GeodesicInterface geodesicInterface, int degree, Tensor target) {
    super(geodesicInterface, degree, target);
    this.lieGroup = lieGroup;
  }

  @Override // from GeodesicBSplineInterpolation
  protected Tensor move(Tensor p, Tensor e, Tensor t) {
    return lieGroup.element(p).combine(lieGroup.element(e).inverse().combine(t));
  }
}
