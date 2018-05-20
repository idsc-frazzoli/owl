// code by jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

/** simple wrapper for obstacle and goal queries
 * implementation is used throughout the repository */
public class SimpleTrajectoryRegionQuery implements TrajectoryRegionQuery, Serializable {
  /** @param region that is queried with tensor = StateTime::state
   * @return */
  public static TrajectoryRegionQuery timeInvariant(Region<Tensor> region) {
    return new SimpleTrajectoryRegionQuery(new TimeInvariantRegion(region));
  }

  // ---
  protected final Region<StateTime> region;

  /** @param region */
  public SimpleTrajectoryRegionQuery(Region<StateTime> region) {
    this.region = region;
  }

  @Override // from TrajectoryRegionQuery
  public final Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectory.stream().filter(this::isMember).findFirst();
  }

  @Override // from TrajectoryRegionQuery
  public boolean isMember(StateTime stateTime) {
    return region.isMember(stateTime);
  }
}
