// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

public final class IntersectionEntryFinder extends TrajectoryEntryFinder {
  public IntersectionEntryFinder(Scalar distance) {
    super(distance);
  }

  @Override // from TrajectoryEntryFinder
  protected Scalar correctedVar(Tensor waypoints, Scalar distance) {
    return distance;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> protected_apply(Tensor waypoints) {
    Tensor waypoints_ = Tensor.of(waypoints.get().stream().map(t -> t.extract(0, 2)));
    return new CircleCurveIntersection(var).string(waypoints_);
  }
}
