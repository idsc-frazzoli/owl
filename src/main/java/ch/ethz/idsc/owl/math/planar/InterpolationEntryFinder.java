// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

import java.util.Optional;

/* package */ class InterpolationEntryFinder implements TrajectoryEntryFinder {
  private final double index;

  public InterpolationEntryFinder(double index) {
    this.index = index;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints) {
    int index_ = (int) index;
    if (waypoints.isPresent())
      if (index_ >= 0 && index_ < waypoints.get().length()) {
        Interpolation interpolation = LinearInterpolation.of(Tensors.of( //
            waypoints.get().get(index_), //
            waypoints.get().get(index_ + 1)));
        return Optional.of(interpolation.at(RealScalar.of(index - index_)));
      }
    return Optional.empty();
  }
}
