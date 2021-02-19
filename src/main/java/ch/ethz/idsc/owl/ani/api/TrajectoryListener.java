// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.List;

import ch.ethz.idsc.owl.math.state.TrajectorySample;

@FunctionalInterface
public interface TrajectoryListener {
  /** @param trajectory */
  void trajectory(List<TrajectorySample> trajectory);
}
