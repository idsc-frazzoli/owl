// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class IntersectionEntryFinder extends TrajectoryEntryFinder {
  public IntersectionEntryFinder(Scalar distance) {
    super(distance);
  }

  @Override // from TrajectoryEntryFinder
  public TrajectoryEntry protected_apply(Tensor waypoints, Scalar distance) {
    Tensor waypoints_ = Tensor.of(waypoints.stream().map(Extract2D.FUNCTION));
    // TODO how to handle heading?
    return new TrajectoryEntry(new SphereCurveIntersection(distance).string(waypoints_), distance);
  }
}
