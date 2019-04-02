// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

/* package */ class NaiveEntryFinder implements TrajectoryEntryFinder {
  private final int index;

  public NaiveEntryFinder(int index) {
    this.index = index;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints) {
    if (waypoints.isPresent())
      return Optional.of(waypoints.get().get(Math.floorMod(index, waypoints.get().length())));
    return Optional.empty();
  }
}
