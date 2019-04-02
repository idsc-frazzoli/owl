package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

public interface TrajectoryEntryFinder {
  /** find trajectory entry point with default variable
   * @param waypoints of reference trajectory
   * @return tensor point {px, py, pa} where trajectory should be entered again */
  Optional<Tensor> apply(Optional<Tensor> waypoints);

  /** find trajectory entry point
   * @param waypoints of reference trajectory
   * @param var to calculate entry point
   * @return tensor point {px, py, pa} where trajectory should be entered again */
  Optional<Tensor> apply(Optional<Tensor> waypoints, Scalar var);
}
