// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** container for TrajectoryEntryFinder results */
public class TrajectoryEntry {
  public final Optional<Tensor> point;
  public final Scalar variable;

  /** @param point found by the TrajectoryEntrFinder
   * @param variable used by the TrajectoryEntrFinder */
  public TrajectoryEntry(Optional<Tensor> point, Scalar variable) {
    this.point = point;
    this.variable = variable;
  }
}
