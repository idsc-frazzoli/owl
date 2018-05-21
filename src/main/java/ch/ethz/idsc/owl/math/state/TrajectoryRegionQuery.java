// code by bapaden and jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.region.Region;

/** performs trajectory containment query
 * 
 * the query is used to check containment of a trajectory in a goal region
 * as well as in an obstacle region */
public interface TrajectoryRegionQuery extends Region<StateTime> {
  /** @param trajectory
   * @return first {@link StateTime} along trajectory that lies inside this region,
   * or Optional.empty() if no state-time in trajectory is member of region */
  Optional<StateTime> firstMember(List<StateTime> trajectory);
}
