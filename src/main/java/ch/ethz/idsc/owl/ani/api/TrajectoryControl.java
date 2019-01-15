// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;

public interface TrajectoryControl extends EntityControl, TrajectoryListener {
  /** @param delay
   * @return trajectory until delay[s] in the future of entity,
   * or current position if entity does not have a trajectory */
  List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay);
}
