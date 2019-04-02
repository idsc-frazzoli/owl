// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class NaiveEntryFinder implements TrajectoryEntryFinder {
  private final int index;

  public NaiveEntryFinder(int index) {
    this.index = index;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Tensor waypoints) {
    // TODO JPH version for string and version for cyclic
    return Optional.of(waypoints.get().get(Math.floorMod(index, waypoints.get().length())));
  }
}
