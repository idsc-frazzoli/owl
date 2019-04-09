// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

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
    Tensor waypoints_ = Tensor.of(waypoints.stream().map(Extract2D.FUNCTION));
    return new CircleCurveIntersection(var).string(waypoints_);
  }
}
