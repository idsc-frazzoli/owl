// code by ynager
package ch.ethz.idsc.owl.glc.core;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

public interface TrajectoryPlanner extends ExpandInterface<GlcNode> {
  /** @param stateTime */
  void insertRoot(StateTime stateTime);

  /** @return best node known to be in goal, or top node in queue, or null,
   * in this order depending on existence */
  Optional<GlcNode> getBestOrElsePeek();

  /** @return state integrator for the state space to generate trajectories from given controls */
  StateIntegrator getStateIntegrator();

  /** @return goal query for the purpose of inspection */
  HeuristicFunction getHeuristicFunction();

  /** @return unmodifiable view on domain map for display and tests */
  Map<Tensor, GlcNode> getDomainMap();

  /** @return unmodifiable view on queue for display and tests */
  Collection<GlcNode> getQueue();
}
