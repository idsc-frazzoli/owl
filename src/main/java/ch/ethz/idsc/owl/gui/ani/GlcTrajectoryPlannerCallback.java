// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.List;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

/**
 * 
 */
// API not finalized
public interface GlcTrajectoryPlannerCallback {
  /** @param head
   * @param trajectoryPlanner */
  void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner);
}
