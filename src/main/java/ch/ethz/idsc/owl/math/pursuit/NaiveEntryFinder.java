// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class NaiveEntryFinder extends TrajectoryEntryFinder {
  public static final TrajectoryEntryFinder INSTANCE = new NaiveEntryFinder();

  // ---
  private NaiveEntryFinder() {
    // ---
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar var) {
    int index = var.number().intValue();
    try {
      return new TrajectoryEntry(Optional.of(waypoints.get(index)), RealScalar.of(index));
    } catch (IndexOutOfBoundsException e) {
      // ---
    }
    return new TrajectoryEntry(Optional.empty(), RealScalar.of(index));
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
