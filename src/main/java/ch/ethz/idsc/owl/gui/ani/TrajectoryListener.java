// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.List;

import ch.ethz.idsc.owl.math.state.TrajectorySample;

public interface TrajectoryListener {
  /** @param trajectory */
  void trajectory(List<TrajectorySample> trajectory);
}
