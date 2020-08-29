// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.region.LinearRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class ApComboRegion implements Region<Tensor>, Serializable {
  /** @param goal = {zCenter, velocityCenter, gammaCenter} defining center of goal region
   * @param radiusVector = {zRadius, velocityCenter, gammaCenter} defining radii of goal region
   * @return new ApComboRegion */
  public static ApComboRegion createApRegion(Tensor goal, Tensor radiusVector) {
    return new ApComboRegion( //
        new LinearRegion(goal.Get(0), radiusVector.Get(0)), //
        new LinearRegion(goal.Get(1), radiusVector.Get(1)), //
        So2Region.periodic(goal.Get(2), radiusVector.Get(2)));
  }

  /***************************************************/
  private final LinearRegion zRegion;
  private final LinearRegion vRegion;
  private final So2Region gammaRegion;

  /* package */ ApComboRegion(LinearRegion zRegion, LinearRegion vRegion, So2Region gammaRegion) {
    this.zRegion = Objects.requireNonNull(zRegion);
    this.vRegion = Objects.requireNonNull(vRegion);
    this.gammaRegion = Objects.requireNonNull(gammaRegion);
  }

  /** function is used to compute heuristic in {@link ApMinTimeGoalManager}
   * 
   * @param tensor {x, z, velocity, pathAngle}
   * @return Euclidean distance from z of tensor to zRegion */
  public final Scalar d_z(Tensor tensor) {
    return zRegion.distance(tensor.Get(1));
  }

  @Override // from Region
  public final boolean isMember(Tensor tensor) { // {x, z, velocity, pathAngle}
    return zRegion.isMember(tensor.get(1)) //
        && vRegion.isMember(tensor.get(2)) //
        && gammaRegion.isMember(tensor.get(3));
  }
}
