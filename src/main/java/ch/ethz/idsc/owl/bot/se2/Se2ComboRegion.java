// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** suggested base class for se2 goal managers.
 * all implemented methods in this layer are final.
 * 
 * class defines circle region for (x, y) component and periodic intervals in angular component */
public class Se2ComboRegion implements Region<Tensor>, Serializable {
  /** @param goal {px, py, angle}
   * @param radiusVector {dist_radius, dist_radius, dist_angle}
   * @throws Exception if first two entries of radiusVector are different */
  public static Se2ComboRegion ball(Tensor goal, Tensor radiusVector) {
    return new Se2ComboRegion( //
        new BallRegion(Extract2D.FUNCTION.apply(goal), RadiusXY.requireSame(radiusVector)), //
        So2Region.periodic(goal.Get(2), radiusVector.Get(2)));
  }

  /** @param goal {px, py, angle}
   * @param semi
   * @param heading non-negative
   * @return */
  public static Se2ComboRegion cone(Tensor goal, Scalar semi, Scalar heading) {
    return new Se2ComboRegion( //
        new ConeRegion(goal, semi), //
        So2Region.periodic(goal.Get(2), heading));
  }

  /***************************************************/
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
    return regionWithDistance.distance(Extract2D.FUNCTION.apply(xya));
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
    return regionWithDistance.isMember(Extract2D.FUNCTION.apply(xya)) && so2Region.isMember(xya.get(2));
  }
}
