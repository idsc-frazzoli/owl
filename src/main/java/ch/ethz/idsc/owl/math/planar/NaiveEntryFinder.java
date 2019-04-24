// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class NaiveEntryFinder extends TrajectoryEntryFinder {
  public NaiveEntryFinder(int initialIndex) {
    super(RealScalar.of(initialIndex));
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar var) {
    int index = var.number().intValue();
    Optional<Tensor> point = Optional.empty();
    try {
      point = Optional.of(waypoints.get(index));
    } catch (IndexOutOfBoundsException e) {
      // ---
    }
    return new TrajectoryEntry(point, RealScalar.of(index));
  }
}
