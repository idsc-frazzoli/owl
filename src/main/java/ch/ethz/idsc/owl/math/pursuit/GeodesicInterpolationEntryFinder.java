// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

public class GeodesicInterpolationEntryFinder extends TrajectoryEntryFinder {
  private static final Mod MOD_UNIT = Mod.function(1);
  // ---
  private final SplitInterface splitInterface;

  /** @param splitInterface non-null */
  public GeodesicInterpolationEntryFinder(SplitInterface splitInterface) {
    this.splitInterface = Objects.requireNonNull(splitInterface);
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar index) {
    // TODO GJOEL use GeodesicInterpolation
    int index_ = index.number().intValue();
    try {
      return new TrajectoryEntry(splitInterface.split( //
          waypoints.get(index_), //
          waypoints.get(index_ + 1), //
          MOD_UNIT.apply(index)), index);
    } catch (IndexOutOfBoundsException e1) {
      try {
        return new TrajectoryEntry(waypoints.get(index_), index);
      } catch (IndexOutOfBoundsException e2) {
        // ---
      }
    }
    return new TrajectoryEntry(null, index);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
