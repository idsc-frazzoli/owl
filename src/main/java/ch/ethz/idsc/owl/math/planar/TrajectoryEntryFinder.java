// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public abstract class TrajectoryEntryFinder {
  private final Scalar initialVar; // uncorrected
  protected Scalar var;

  public TrajectoryEntryFinder(Scalar initialVar) {
    this.initialVar = initialVar;
  }

  /** @param waypoints of trajectory
   * @return function to be applied on waypoints */
  public Function<Scalar, Optional<Tensor>> on(Tensor waypoints) {
    return scalar -> {
      var = correctedVar(waypoints, scalar);
      return protected_apply(waypoints);
    };
  }

  /** @param waypoints of trajectory
   * @return trajectory entry point */
  public Optional<Tensor> initial(Tensor waypoints) {
    return on(waypoints).apply(initialVar);
  }

  /** WARNING this might not be the variable actually applied in initial()
   * @return initial variable */
  public Scalar uncorrectedInitialVar() {
    return initialVar;
  }

  /** @return last applied variable */
  public Scalar currentVar() {
    return var;
  }

  /** @param waypoints of trajectory
   * @return of trajectory entry point */
  abstract Optional<Tensor> protected_apply(Tensor waypoints);

  /** @param var to specify entry point choice
   * @return actual variable to be used */
  abstract Scalar correctedVar(Tensor waypoints, Scalar var);
}
