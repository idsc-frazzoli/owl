// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.List;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

public interface GlcPlannerCallback {
  /** @param head
   * @param trajectoryPlanner with a trajectory from the last {@link StateTime} in head */
  void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner);
}
