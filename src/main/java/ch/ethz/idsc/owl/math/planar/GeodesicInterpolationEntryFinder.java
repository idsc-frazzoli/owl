// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class GeodesicInterpolationEntryFinder implements TrajectoryEntryFinder {
  private final GeodesicInterface geodesicInterface;
  private final double index;

  public GeodesicInterpolationEntryFinder(GeodesicInterface geodesicInterface, double index) {
    this.geodesicInterface = geodesicInterface;
    this.index = index;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints) {
    int index_ = (int) index;
    if (waypoints.isPresent())
      if (index_ >= 0 && index_ < waypoints.get().length())
        return Optional.of(geodesicInterface.split( //
            waypoints.get().get(index_), //
            waypoints.get().get(index_ + 1), //
            RealScalar.of(index - index_)));
    return Optional.empty();
  }
}
