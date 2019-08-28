// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.crv.clothoid.OriginClothoid.Curve;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** clothoid3 factory
 *
 * <p>The curvature of the provided curve resembles that of a clothoid.
 * The curve does not interpolate the tangents at the end points p and q in general.
 *
 * <p>In order to obtain samples of a clothoid that interpolates p and q the
 * recommended method is to use {@link LaneRiesenfeldCurveSubdivision} with
 * Clothoid3 and degrees 1 or 3. */
public enum PolarClothoids implements GeodesicInterface {
  INSTANCE;
  // ---
  public static final CurveSubdivision CURVE_SUBDIVISION = LaneRiesenfeldCurveSubdivision.of(INSTANCE, 1);

  @Override // from GeodesicInterface
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    LieGroupElement lieGroupElement = Se2CoveringGroup.INSTANCE.element(p);
    Curve curve = new OriginClothoid(lieGroupElement.inverse().combine(q)).new Curve();
    return t -> lieGroupElement.combine(curve.apply(t));
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar t) {
    return curve(p, q).apply(t);
  }
}
