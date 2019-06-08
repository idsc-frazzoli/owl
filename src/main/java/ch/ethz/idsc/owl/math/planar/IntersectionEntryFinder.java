// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Norm;

public final class IntersectionEntryFinder extends TrajectoryEntryFinder {
  public static final TrajectoryEntryFinder INSTANCE = new IntersectionEntryFinder();

  // ---
  // TODO OWL 044 JPH make constructor private
  public IntersectionEntryFinder() {
    // ---
  }

  @Override // from TrajectoryEntryFinder
  public TrajectoryEntry protected_apply(Tensor waypoints, Scalar variable) {
    AssistedCurveIntersection intersection = waypoints.stream().allMatch(t -> VectorQ.ofLength(t, 2)) ? //
        new SphereCurveIntersection(variable) : //
        new PseudoSe2CurveIntersection(variable);
    return new TrajectoryEntry(intersection.string(waypoints), variable);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    MinMax minmax = MinMax.of(Tensor.of(waypoints.stream().map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    Interpolation interpolation = LinearInterpolation.of(Tensors.of(minmax.min(), minmax.max()));
    return IntStream.range(0, waypoints.length() + 1) //
        .mapToObj(i -> RationalScalar.of(i, waypoints.length())) //
        .map(interpolation::At);
  }
}
