// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Floor;

/* package */ class GeodesicInterpolationEntryFinder implements TrajectoryEntryFinder {
  private final GeodesicInterface geodesicInterface;
  private final Scalar index;

  public GeodesicInterpolationEntryFinder(GeodesicInterface geodesicInterface, Scalar index) {
    this.geodesicInterface = geodesicInterface;
    this.index = index;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Tensor waypoints) {
    if (Clips.interval(0, waypoints.length()).isInside(index)) {
      Scalar floor = Floor.FUNCTION.apply(index);
      if (floor.equals(RealScalar.of(waypoints.length())))
        return Optional.of(Last.of(waypoints));
      return Optional.of(geodesicInterface.split( //
          waypoints.get(floor.number().intValue()), //
          waypoints.get(floor.number().intValue() + 1), //
          index.subtract(floor)));
    }
    return Optional.empty();
  }
}
