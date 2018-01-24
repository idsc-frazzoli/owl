// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.tensor.Scalar;

public interface TrajectoryControl extends EntityControl {
  List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay);

  void setTrajectory(List<TrajectorySample> trajectory);
}
