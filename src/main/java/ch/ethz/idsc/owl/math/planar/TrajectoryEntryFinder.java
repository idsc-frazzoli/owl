package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

public interface TrajectoryEntryFinder {

  Optional<Tensor> apply(Optional<Tensor> waypoints);

  Optional<Tensor> apply(Optional<Tensor> waypoints, Number index);
}
