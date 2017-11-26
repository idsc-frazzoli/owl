// code by bapaden and jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;
import java.util.Optional;

/** instance encodes an empty trajectory region
 * 
 * all intersection queries with a trajectory return: "empty intersection" */
public enum EmptyTrajectoryRegionQuery implements TrajectoryRegionQuery {
  INSTANCE;
  // ---
  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return Optional.empty();
  }

  @Override // from TrajectoryRegionQuery
  public boolean isMember(StateTime stateTime) {
    return false;
  }
}
