package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

/* package */ enum NaiveEntryFinder implements TrajectoryEntryFinder {
  ;

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints) {
    return apply(waypoints, 0);
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints, Number index) {
    int index_ = index.intValue();
    if (waypoints.isPresent() && index_ >= 0)
      return Optional.of(waypoints.get().get(index_));
    return Optional.empty();
  }
}
