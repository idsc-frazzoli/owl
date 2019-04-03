// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;

import java.util.Optional;

public final class InterpolationEntryFinder extends TrajectoryEntryFinder {
  public InterpolationEntryFinder(double initialIndex) {
    super(RealScalar.of(initialIndex));
  }

  @Override // from TrajectoryEntryFinder
  protected Scalar correctedVar(Tensor waypoints, Scalar index) {
    return index;
  }

  @Override // from TrajectoryEntryFinder
  protected Optional<Tensor> protected_apply(Tensor waypoints) {
    int index_ = var.number().intValue();
    if (index_ >= 0 && index_ < waypoints.get().length()) {
      Interpolation interpolation = LinearInterpolation.of(Tensors.of( //
          waypoints.get().get(index_), //
          waypoints.get().get(index_ + 1)));
      return Optional.of(interpolation.at(Mod.function(1).apply(var)));
    }
    return Optional.empty();
  }
}
