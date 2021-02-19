// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.sophus.math.MinMax;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.itp.Interpolation;
import ch.ethz.idsc.tensor.itp.LinearInterpolation;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

public final class IntersectionEntryFinder extends TrajectoryEntryFinder implements Serializable {
  public static final TrajectoryEntryFinder SPHERE_RN = new IntersectionEntryFinder(SphereCurveIntersection::new);
  public static final TrajectoryEntryFinder SPHERE_SE2 = new IntersectionEntryFinder(SphereSe2CurveIntersection::new);
  // ---
  private final ScalarAssistedCurveIntersectionFunction function;

  public IntersectionEntryFinder(ScalarAssistedCurveIntersectionFunction function) {
    this.function = function;
  }

  @Override // from TrajectoryEntryFinder
  public TrajectoryEntry protected_apply(Tensor waypoints, Scalar radius) {
    return new TrajectoryEntry(function.apply(radius).string(waypoints).orElse(null), radius);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    MinMax minmax = MinMax.of(Tensor.of(waypoints.stream().map(Extract2D.FUNCTION).map(Vector2Norm::of)));
    Interpolation interpolation = LinearInterpolation.of(Tensors.of(minmax.min(), minmax.max()));
    return IntStream.range(0, waypoints.length()) //
        .mapToObj(i -> RationalScalar.of(i, waypoints.length() - 1)) //
        .map(interpolation::At);
  }
}
