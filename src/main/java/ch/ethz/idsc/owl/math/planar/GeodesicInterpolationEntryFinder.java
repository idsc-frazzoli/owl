// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

import java.util.Optional;

public final class GeodesicInterpolationEntryFinder extends TrajectoryEntryFinder {
  private final GeodesicInterface geodesicInterface;

  public GeodesicInterpolationEntryFinder(double initialIndex, GeodesicInterface geodesicInterface) {
    super(RealScalar.of(initialIndex));
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from TrajectoryEntryFinder
  protected Scalar correctedVar(Tensor waypoints, Scalar index) {
    return index;
  }

  @Override // from TrajectoryEntryFinder
  protected Optional<Tensor> protected_apply(Tensor waypoints) {
    int index_ = var.number().intValue();
    if (index_ >= 0 && index_ < waypoints.get().length())
      return Optional.of(geodesicInterface.split( //
          waypoints.get().get(index_), //
          waypoints.get().get(index_ + 1), //
          Mod.function(1).apply(var)));
    return Optional.empty();
  }
}
