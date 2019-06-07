// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class NaiveEntryFinder extends TrajectoryEntryFinder {
  public static final TrajectoryEntryFinder INSTANCE = new NaiveEntryFinder();

  // ---
  // TODO OWL 044 JPH make constructor private
  public NaiveEntryFinder() {
    // ---
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar var) {
    int index = var.number().intValue();
    Optional<Tensor> point = Optional.empty();
    try {
      point = Optional.of(waypoints.get(index));
    } catch (IndexOutOfBoundsException e) {
      // ---
    }
    return new TrajectoryEntry(point, RealScalar.of(index));
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
