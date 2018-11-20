// code by astoll
package ch.ethz.idsc.owl.bot.ap;

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

public class ApComboRegion implements Region<Tensor>, Serializable {
  /** @param goal {velocity, pathAngle, x, z}
   * @param radiusVector {dist_radius, dist_radius, dist_angle, dist_v}
   * @throws Exception if first two entries of radiusVector are different */
  public static ApComboRegion spherical(Tensor goal, Tensor radiusVector) {
    return new ApComboRegion( //
        new SphericalRegion(goal.extract(2, 4), RadiusXY.requireSame(radiusVector.extract(2, 4))), //
        new So2Region(goal.Get(1), radiusVector.Get(2)), new LinearRegion(goal.Get(0), radiusVector.Get(3)));
  }

  private final RegionWithDistance<Tensor> regionWithDistance;
  private final So2Region so2Region;
  private final LinearRegion linearRegion;

  public ApComboRegion(RegionWithDistance<Tensor> regionWithDistance, So2Region so2Region, LinearRegion linearRegion) {
    this.regionWithDistance = Objects.requireNonNull(regionWithDistance);
    this.so2Region = Objects.requireNonNull(so2Region);
    this.linearRegion = Objects.requireNonNull(linearRegion);
  }

  /** function is used to compute heuristic in {@link ApMinTimeGoalManager}
   * 
   * @param tensor {velocity, pathAngle, x, z}
   * @return Euclidean distance from x, z of tensor to spherical region */
  public final Scalar d_xz(Tensor tensor) {
    return regionWithDistance.distance(tensor.extract(2, 4));
  }

  @Override // from Region
  public boolean isMember(Tensor goal) {
    return regionWithDistance.isMember(goal.extract(2, 4)) && so2Region.isMember(goal.get(1)) && linearRegion.isMember(goal.get(0));
  }
}
