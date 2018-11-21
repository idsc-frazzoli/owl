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
  /** @param goal {x, z, velocity, pathAngle}
   * @param radiusVector {dist_radius, dist_radius, dist_v, dist_angle}
   * @throws Exception if first two entries of radiusVector are different */
  public static ApComboRegion spherical(Tensor goal, Tensor radiusVector) {
    return new ApComboRegion( //
        // <<<<<<< HEAD
        // new SphericalRegion(goal.extract(2, 4), RadiusXY.requireSame(radiusVector.extract(2, 4))), //
        new LinearRegion(goal.Get(2), radiusVector.Get(2)), //
        new LinearRegion(goal.Get(3), radiusVector.Get(3)), //
        new So2Region(goal.Get(1), radiusVector.Get(1)), //
        new LinearRegion(goal.Get(0), radiusVector.Get(0)));
    // =======
    // new SphericalRegion(goal.extract(0, 2), RadiusXY.requireSame(radiusVector.extract(0, 2))), //
    // new So2Region(goal.Get(3), radiusVector.Get(3)), new LinearRegion(goal.Get(2), radiusVector.Get(2)));
    // >>>>>>> master
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
    // <<<<<<< HEAD
    return Norm._2.between(tensor.extract(2, 4), Tensors.of(xRegion.center(), zRegion.center()));
    // =======
    // return regionWithDistance.distance(tensor.extract(0, 2));
  }

  public final Scalar d_z(Tensor tensor) {
    return tensor.Get(1).abs();
    // >>>>>>> master
  }

  @Override // from Region
  public boolean isMember(Tensor goal) {
    // <<<<<<< HEAD
    return vRegion.isMember(goal.get(0)) //
        && so2Region.isMember(goal.get(2)) //
        && xRegion.isMember(goal.get(2)) //
        && zRegion.isMember(goal.get(3));
    // =======
    // return regionWithDistance.isMember(goal.extract(0, 2)) && so2Region.isMember(goal.get(3)) && linearRegion.isMember(goal.get(2));
    // >>>>>>> master
  }
}
