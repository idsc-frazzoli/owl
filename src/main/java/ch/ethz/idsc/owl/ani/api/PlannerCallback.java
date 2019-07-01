// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

@FunctionalInterface
public interface PlannerCallback<T extends TrajectoryPlanner> {
  /** @param head
   * @param trajectoryPlanner with a trajectory from the last {@link StateTime} in head */
  void expandResult(List<TrajectorySample> head, T trajectoryPlanner);
}
