// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.BijectionFamily;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.ply.EllipsePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;

/** ellipsoid region that is moving with respect to time */
public class R2xTEllipsoidStateTimeRegion implements Region<StateTime>, RenderInterface, Serializable {
  /** number of samples to visualize ellipsoid */
  private static final int RESOLUTION = 22;
  // ---
  private final Tensor invert;
  private final Tensor polygon;
  private final BijectionFamily bijectionFamily;
  private final Supplier<Scalar> supplier;

  /** @param radius encodes principle axis of ellipsoid region
   * @param bijectionFamily with origin at center of ellipsoid region
   * @param supplier for parameter to evaluate bijectionFamily */
  public R2xTEllipsoidStateTimeRegion(Tensor radius, BijectionFamily bijectionFamily, Supplier<Scalar> supplier) {
    invert = radius.map(Scalar::reciprocal);
    this.bijectionFamily = bijectionFamily;
    this.supplier = supplier;
    polygon = EllipsePoints.of(RESOLUTION, Extract2D.FUNCTION.apply(radius));
  }

  @Override // from Region
  public boolean isMember(StateTime stateTime) {
    Tensor state = stateTime.state().extract(0, invert.length());
    Scalar time = stateTime.time();
    TensorUnaryOperator rev = bijectionFamily.inverse(time);
    return Scalars.lessEquals(Vector2NormSquared.of(rev.apply(state).pmul(invert)), RealScalar.ONE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Scalar time = supplier.get();
    TensorUnaryOperator fwd = bijectionFamily.forward(time);
    Path2D path2D = geometricLayer.toPath2D(Tensor.of(polygon.stream().map(fwd)));
    path2D.closePath();
    graphics.setColor(RegionRenders.COLOR);
    graphics.fill(path2D);
    graphics.setColor(RegionRenders.BOUNDARY);
    graphics.draw(path2D);
  }
}
