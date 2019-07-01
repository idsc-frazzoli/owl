// code by ynager
package ch.ethz.idsc.owl.glc.core;

import java.util.Map;

import ch.ethz.idsc.owl.ani.api.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.tensor.Tensor;

public interface GlcTrajectoryPlanner extends TrajectoryPlanner<GlcNode> {
  /** @return state integrator for the state space to generate trajectories from given controls */
  StateIntegrator getStateIntegrator();

  /** @return goal query for the purpose of inspection */
  HeuristicFunction getHeuristicFunction();

  /** @return unmodifiable view on domain map for display and tests */
  Map<Tensor, GlcNode> getDomainMap();
}
