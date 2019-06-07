// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;

public final class InterpolationEntryFinder extends TrajectoryEntryFinder {
  public static final TrajectoryEntryFinder INSTANCE = new InterpolationEntryFinder();
  // ---
  private static final Mod MOD_UNIT = Mod.function(1);

  // TODO OWL 044 JPH make constructor private
  public InterpolationEntryFinder() {
    // ---
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

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
