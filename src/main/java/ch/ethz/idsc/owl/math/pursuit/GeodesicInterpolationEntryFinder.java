// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.sophus.itp.GeodesicInterpolation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.itp.BinaryAverage;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class GeodesicInterpolationEntryFinder extends TrajectoryEntryFinder implements Serializable {
  private final BinaryAverage binaryAverage;

  /** @param binaryAverage non-null */
  public GeodesicInterpolationEntryFinder(BinaryAverage binaryAverage) {
    this.binaryAverage = Objects.requireNonNull(binaryAverage);
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar index) {
    Clip clip = Clips.positive(waypoints.length() - 1);
    return new TrajectoryEntry(clip.isInside(index) //
        ? GeodesicInterpolation.of(binaryAverage, waypoints).at(index)
        : null, index);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
