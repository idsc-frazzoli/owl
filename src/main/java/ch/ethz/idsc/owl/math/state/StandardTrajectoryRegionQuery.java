// code by jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.VoidStateTimeRegionMembers;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

public class StandardTrajectoryRegionQuery implements TrajectoryRegionQuery, Serializable {
  /** @param region that is queried with tensor = StateTime::state
   * @return */
  public static TrajectoryRegionQuery timeInvariant(Region<Tensor> region) {
    return new StandardTrajectoryRegionQuery(new TimeInvariantRegion(region), VoidStateTimeRegionMembers.INSTANCE);
  }

  // ---
  private final Region<StateTime> region;
  private final StateTimeRegionCallback stateTimeRegionCallback;

  public StandardTrajectoryRegionQuery(Region<StateTime> region, StateTimeRegionCallback stateTimeRegionCallback) {
    this.region = region;
    this.stateTimeRegionCallback = stateTimeRegionCallback;
  }

  @Override // from TrajectoryRegionQuery
  public final Optional<StateTime> firstMember(List<StateTime> trajectory) {
    for (StateTime stateTime : trajectory)
      if (region.isMember(stateTime)) {
        stateTimeRegionCallback.notify_isMember(stateTime);
        return Optional.of(stateTime);
      }
    return Optional.empty();
  }

  @Override // from TrajectoryRegionQuery
  public final boolean isMember(StateTime stateTime) {
    boolean isMember = region.isMember(stateTime);
    if (isMember)
      stateTimeRegionCallback.notify_isMember(stateTime);
    return isMember;
  }

  public StateTimeRegionCallback getStateTimeRegionCallback() {
    return stateTimeRegionCallback;
  }
}
