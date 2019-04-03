// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public abstract class TrajectoryEntryFinder {
  private final Scalar initialVar;
  protected Scalar var;

  public TrajectoryEntryFinder(Scalar initialVar) {
    this.initialVar = initialVar;
    var = initialVar;
  }

  /** @param waypoints of trajectory
   * @return function to be applied on waypoints */
  public Function<Scalar, Optional<Tensor>> on(Tensor waypoints) {
    return s -> {
      var = correctedVar(waypoints.get(), s);
      return protected_apply(waypoints.get());
    };
  }

  /** @param waypoints of trajectory
   * @return trajectory entry point */
  public Optional<Tensor> initial(Tensor waypoints) {
    return on(waypoints).apply(initialVar);
  }

  /** @return last applied variable */
  public Optional<Scalar> currentVar() {
    return Optional.of(var);
  }

  /** @param waypoints of trajectory
   * @return of trajectory entry point */
  abstract Optional<Tensor> protected_apply(Tensor waypoints);

  /** @param var to specify entry point choice
   * @return actual variable to be used */
  abstract Scalar correctedVar(Tensor waypoints, Scalar var);
}
