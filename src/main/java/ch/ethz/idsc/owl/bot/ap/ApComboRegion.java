// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.region.LinearRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public class ApComboRegion implements Region<Tensor>, Serializable {
  /** @param goal {velocity, pathAngle, x, z}
   * @param radiusVector {dist_radius, dist_radius, dist_angle, dist_v}
   * @throws Exception if first two entries of radiusVector are different */
  public static ApComboRegion spherical(Tensor goal, Tensor radiusVector) {
    return new ApComboRegion( //
        // new SphericalRegion(goal.extract(2, 4), RadiusXY.requireSame(radiusVector.extract(2, 4))), //
        new LinearRegion(goal.Get(2), radiusVector.Get(2)), //
        new LinearRegion(goal.Get(3), radiusVector.Get(3)), //
        new So2Region(goal.Get(1), radiusVector.Get(1)), //
        new LinearRegion(goal.Get(0), radiusVector.Get(0)));
  }

  private final So2Region so2Region;
  private final LinearRegion xRegion;
  private final LinearRegion zRegion;
  private final LinearRegion vRegion;

  public ApComboRegion(LinearRegion xRegion, LinearRegion zRegion, So2Region so2Region, LinearRegion vRegion) {
    this.so2Region = Objects.requireNonNull(so2Region);
    this.vRegion = Objects.requireNonNull(vRegion);
    this.xRegion = Objects.requireNonNull(xRegion);
    this.zRegion = Objects.requireNonNull(zRegion);
  }

  /** function is used to compute heuristic in {@link ApMinTimeGoalManager}
   * 
   * @param tensor {velocity, pathAngle, x, z}
   * @return Euclidean distance from x, z of tensor to spherical region */
  public final Scalar d_xz(Tensor tensor) {
    return Norm._2.between(tensor.extract(2, 4), Tensors.of(xRegion.center(), zRegion.center()));
  }

  @Override // from Region
  public boolean isMember(Tensor goal) {
    return vRegion.isMember(goal.get(0)) //
        && so2Region.isMember(goal.get(2)) //
        && xRegion.isMember(goal.get(2)) //
        && zRegion.isMember(goal.get(3));
  }
}
