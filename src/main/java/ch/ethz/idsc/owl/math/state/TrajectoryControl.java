// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface TrajectoryControl {
  Tensor control(StateTime tail, Scalar now);

  List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay);

  void setTrajectory(List<TrajectorySample> trajectory);
}
