package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

public enum IntersectionEntryFinder implements TrajectoryEntryFinder {
  ;

  private static final Scalar defaultDistance = RealScalar.of(3);

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints) {
    return apply(waypoints, defaultDistance);
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints, Scalar distance) {
    if (waypoints.isPresent())
      return new CircleCurveIntersection(distance).string(waypoints.get());
    return Optional.empty();
  }
}
