// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class InterpolationEntryFinder implements TrajectoryEntryFinder {
  private final Scalar index;

  public InterpolationEntryFinder(Scalar index) {
    this.index = index;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Tensor waypoints) {
    if (Clips.interval(0, waypoints.length() - 1).isInside(index))
      return Optional.of(LinearInterpolation.of(waypoints).at(index));
    return Optional.empty();
  }
}
