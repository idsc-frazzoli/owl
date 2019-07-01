// code by ynager
package ch.ethz.idsc.owl.ani.api;

import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.ExpandInterface;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;

public interface TrajectoryPlanner<T extends StateCostNode> extends ExpandInterface<T>  {
  /** @param stateTime */
  void insertRoot(StateTime stateTime);

  /** @return best node known to be in goal, or top node in queue, or null,
   * in this order depending on existence */
  Optional<T> getBestOrElsePeek();

  /** @return state integrator for the state space to generate trajectories from given controls */
  StateIntegrator getStateIntegrator();

  /** @return unmodifiable view on queue for display and tests */
  Collection<T> getQueue();
}
