// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;

public final class InterpolationEntryFinder extends TrajectoryEntryFinder {
  private static final Mod MOD_UNIT = Mod.function(1);

  // ---
  public InterpolationEntryFinder(double initialIndex) {
    super(RealScalar.of(initialIndex));
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar index) {
    int index_ = index.number().intValue();
    Optional<Tensor> point = Optional.empty();
    try {
      Interpolation interpolation = LinearInterpolation.of(Tensors.of( //
          waypoints.get(index_), //
          waypoints.get(index_ + 1)));
      point = Optional.of(interpolation.at(MOD_UNIT.apply(index)));
    } catch (IndexOutOfBoundsException e1) {
      try {
        point = Optional.of(waypoints.get(index_));
      } catch (IndexOutOfBoundsException e2) {
        // ---
      }
    }
    return new TrajectoryEntry(point, index);
  }
}
