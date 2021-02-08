// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** container for TrajectoryEntryFinder results */
public final class TrajectoryEntry implements Serializable {
  private final Tensor point;
  private final Scalar variable;

  /** @param point found by the TrajectoryEntryFinder, may be null
   * @param variable used by the TrajectoryEntryFinder */
  public TrajectoryEntry(Tensor point, Scalar variable) {
    this.point = point;
    this.variable = variable;
  }

  public Optional<Tensor> point() {
    return Optional.ofNullable(point);
  }

  public Scalar variable() {
    return variable;
  }
}
