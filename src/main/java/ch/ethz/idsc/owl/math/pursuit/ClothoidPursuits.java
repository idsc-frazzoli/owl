// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;
import java.util.stream.Stream;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Nest;

public enum ClothoidPursuits {
  ;
  /** @param lookAhead of the form {x, y, heading}
   * @param depth
   * @return curve of ((1 << depth) + 1) points in SE(2) from origin {0, 0, 0} to given lookAhead */
  public static Tensor curve(Tensor lookAhead, int depth) {
    return Nest.of( //
        ClothoidTerminalRatios.CURVE_SUBDIVISION::string, //
        Tensor.of(Stream.of(lookAhead.map(Scalar::zero), lookAhead)), //
        depth);
  }

  /** @param tensor waypoints
   * @param trajectoryEntryFinder strategy
   * @param var
   * @return GeodesicPursuit */
  public static GeodesicPursuitInterface fromTrajectory( //
      Tensor tensor, TrajectoryEntryFinder trajectoryEntryFinder, Scalar var) {
    Optional<Tensor> lookAhead = trajectoryEntryFinder.on(tensor).apply(var).point();
    return lookAhead.isPresent() //
        ? new ClothoidPursuit(lookAhead.get())
        : VoidPursuit.INSTANCE;
  }
}
