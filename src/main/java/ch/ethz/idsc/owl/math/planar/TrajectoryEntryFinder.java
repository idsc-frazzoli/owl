// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

public interface TrajectoryEntryFinder {
  /** find trajectory entry point with default variable
   * @param waypoints of reference trajectory
   * @return tensor point {px, py, pa} where trajectory should be entered again */
  Optional<Tensor> apply(Optional<Tensor> waypoints);
}
