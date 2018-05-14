// code by jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

public class StandardTrajectoryRegionQuery implements TrajectoryRegionQuery, Serializable {
  /** @param region that is queried with tensor = StateTime::state
   * @return */
  public static TrajectoryRegionQuery timeInvariant(Region<Tensor> region) {
    return new StandardTrajectoryRegionQuery(new TimeInvariantRegion(region));
  }

  // ---
  private final Region<StateTime> region;

  public StandardTrajectoryRegionQuery(Region<StateTime> region) {
    this.region = region;
  }

  @Override // from TrajectoryRegionQuery
  public final Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectory.stream().filter(region::isMember).findFirst();
  }

  @Override // from TrajectoryRegionQuery
  public final boolean isMember(StateTime stateTime) {
    return region.isMember(stateTime);
  }
}
