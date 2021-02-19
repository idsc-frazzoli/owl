// code by jph, ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Objects;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.LinearRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;

/** suggested base class for tse2 goal managers.
 * all implemented methods in this layer are final.
 * 
 * class defines circle region for (x, y) component, periodic intervals in angular component, linear region in v */
public class Tse2ComboRegion extends Se2ComboRegion {
  /** @param goal {px, py, angle, v}
   * @param radiusVector {dist_radius, dist_radius, dist_angle, dist_v}
   * @throws Exception if first two entries of radiusVector are different */
  public static Tse2ComboRegion spherical(Tensor goal, Tensor radiusVector) {
    return new Tse2ComboRegion( //
        new BallRegion(Extract2D.FUNCTION.apply(goal), RadiusXY.requireSame(radiusVector)), //
        So2Region.periodic(goal.Get(2), radiusVector.Get(2)), //
        new LinearRegion(goal.Get(3), radiusVector.Get(3)));
  }

  /***************************************************/
  private final LinearRegion linearRegion;

  /** @param regionWithDistance for xy
   * @param so2Region for angle
   * @param linearRegion for velocity */
  public Tse2ComboRegion(RegionWithDistance<Tensor> regionWithDistance, So2Region so2Region, LinearRegion linearRegion) {
    super(regionWithDistance, so2Region);
    this.linearRegion = Objects.requireNonNull(linearRegion);
  }

  /** function used in computation of heuristic {@link Tse2MinTimeGoalManager}
   * 
   * @param xyav == {px, py, angle, vel}
   * @return distance of velocity from linearRegion */
  protected final Scalar d_vel(Tensor xyav) {
    return linearRegion.distance(xyav.get(3));
  }

  @Override // from Region
  public final boolean isMember(Tensor xyav) {
    return super.isMember(xyav) && linearRegion.isMember(xyav.get(3));
  }

  public Clip v_range() {
    return linearRegion.clip();
  }
}
