// code by jph, ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.owl.math.region.LinearRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** suggested base class for tse2 goal managers.
 * all implemented methods in this layer are final.
 * 
 * class defines circle region for (x,y) component, periodic intervals in angular component, linear region in v */
public class Tse2ComboRegion implements Region<Tensor>, Serializable {
  /** @param goal {px, py, angle, v}
   * @param radiusVector {dist_radius, dist_radius, dist_angle}
   * @throws Exception if first two entries of radiusVector are different */
  public static Tse2ComboRegion spherical(Tensor goal, Tensor radiusVector) {
    return new Tse2ComboRegion( //
        new SphericalRegion(goal.extract(0, 2), RadiusXY.requireSame(radiusVector)), //
        new So2Region(goal.Get(2), radiusVector.Get(2)), //
        new LinearRegion(goal.Get(3), radiusVector.Get(3)));
  }

  // ---
  private final RegionWithDistance<Tensor> xyRegion;
  private final So2Region angleRegion;
  private final RegionWithDistance<Tensor> velRegion;

  public Tse2ComboRegion(RegionWithDistance<Tensor> xyRegion, So2Region angleRegion, RegionWithDistance<Tensor> velRegion) {
    this.xyRegion = Objects.requireNonNull(xyRegion);
    this.angleRegion = Objects.requireNonNull(angleRegion);
    this.velRegion = Objects.requireNonNull(velRegion);
  }

  /** @param xyav == {px, py, angle, vel}
   * @return distance of {px, py} from spherical region */
  protected final Scalar d_xy(Tensor xyav) {
    return xyRegion.distance(xyav.extract(0, 2));
  }

  /** @param xyav == {px, py, angle, vel}
   * @return distance of angle from so2region */
  protected final Scalar d_angle(Tensor xyav) {
    return angleRegion.distance(xyav.get(2));
  }

  /** @param xyav == {px, py, angle, vel}
   * @return distance of velocity from so2region */
  protected final Scalar d_vel(Tensor xyav) {
    return velRegion.distance(xyav.get(3));
  }

  @Override // from Region
  public final boolean isMember(Tensor xyav) {
    return xyRegion.isMember(xyav.extract(0, 2)) //
        && angleRegion.isMember(xyav.get(2)) //
        && velRegion.isMember(xyav.get(3));
  }
}
