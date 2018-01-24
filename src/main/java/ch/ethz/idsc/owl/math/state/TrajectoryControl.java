// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.tensor.Scalar;

public interface TrajectoryControl {
  void integrate(Scalar now);

  StateTime getStateTimeNow();

  List<TrajectorySample> getFutureTrajectoryUntil(Scalar delay);
}
