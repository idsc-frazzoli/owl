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
  protected Scalar correctedVar(Tensor waypoints, Scalar index) {
    return index;
  }

  @Override // from TrajectoryEntryFinder
  protected Optional<Tensor> protected_apply(Tensor waypoints) {
    int index_ = var.number().intValue();
    try {
      Interpolation interpolation = LinearInterpolation.of(Tensors.of( //
          waypoints.get(index_), //
          waypoints.get(index_ + 1)));
      return Optional.of(interpolation.at(MOD_UNIT.apply(var)));
    } catch (IndexOutOfBoundsException e) {
      return Optional.empty();
    }
  }
}
