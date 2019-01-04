// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** suggested base class for se2 goal managers.
 * all implemented methods in this layer are final.
 * 
 * class defines circle region for (x,y) component and periodic intervals in angular component */
public class Se2ComboRegion implements Region<Tensor>, Serializable {
  /** @param goal {px, py, angle}
   * @param radiusVector {dist_radius, dist_radius, dist_angle}
   * @throws Exception if first two entries of radiusVector are different */
  public static Se2ComboRegion spherical(Tensor goal, Tensor radiusVector) {
    return new Se2ComboRegion( //
        new SphericalRegion(goal.extract(0, 2), RadiusXY.requireSame(radiusVector)), //
        new So2Region(goal.Get(2), radiusVector.Get(2)));
  }

  /** @param goal {px, py, angle}
   * @param semi
   * @param radius
   * @return */
  public static Se2ComboRegion cone(Tensor goal, Scalar semi, Scalar radius) {
    return new Se2ComboRegion( //
        new ConeRegion(goal, semi), //
        new So2Region(goal.Get(2), radius));
  }
  // ---

  private final RegionWithDistance<Tensor> regionWithDistance;
  private final So2Region so2Region;

  public Se2ComboRegion(RegionWithDistance<Tensor> regionWithDistance, So2Region so2Region) {
    this.regionWithDistance = Objects.requireNonNull(regionWithDistance);
    this.so2Region = Objects.requireNonNull(so2Region);
  }

  /** function is used to compute heuristic in {@link Se2MinTimeGoalManager}
   * 
   * @param xya == {px, py, angle}
   * @return distance of {px, py} from spherical region */
  public final Scalar d_xy(Tensor xya) {
    return regionWithDistance.distance(xya.extract(0, 2));
  }

  /** function is used to compute heuristic in {@link Se2MinTimeGoalManager}
   * 
   * @param xya == {px, py, angle}
   * @return distance of angle from so2region */
  public final Scalar d_angle(Tensor xya) {
    return so2Region.distance(xya.get(2));
  }

  @Override // from Region
  public boolean isMember(Tensor xya) {
    // only the first three entries of xya are considered
    return regionWithDistance.isMember(xya.extract(0, 2)) && so2Region.isMember(xya.get(2));
  }
}
