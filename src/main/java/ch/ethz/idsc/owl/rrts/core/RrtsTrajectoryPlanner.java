// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.TrajectoryPlanner;
import ch.ethz.idsc.owl.rrts.RrtsPlannerProcess;

public interface RrtsTrajectoryPlanner extends TrajectoryPlanner<RrtsNode> {
  /** @return {@link RrtsPlannerProcess} or null if not properly set up */
  Optional<RrtsPlannerProcess> getProcess();
}
