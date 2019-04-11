// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public abstract class TrajectoryEntryFinder {
  private final Scalar initialVar; // uncorrected

  public TrajectoryEntryFinder(Scalar initialVar) {
    this.initialVar = initialVar;
  }

  /** @param waypoints of trajectory
   * @return function to be applied on waypoints */
  public Function<Scalar, TrajectoryEntry> on(Tensor waypoints) {
    return scalar -> protected_apply(waypoints, scalar);
  }

  /** @param waypoints of trajectory
   * @return TrajectoryEntry */
  public TrajectoryEntry initial(Tensor waypoints) {
    return on(waypoints).apply(initialVar);
  }

  /** WARNING this might not be the variable actually applied in initial()
   * @return initial variable */
  public Scalar uncorrectedInitialVar() {
    return initialVar;
  }

  /** @param waypoints of trajectory
   * @param var to specify entry point choice
   * @return TrajectoryEntry */
  abstract TrajectoryEntry protected_apply(Tensor waypoints, Scalar var);
}
