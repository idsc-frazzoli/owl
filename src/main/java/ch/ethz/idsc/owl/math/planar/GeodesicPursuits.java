// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum GeodesicPursuits {
  ;
  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param tensor waypoints
   * @param entryFinder strategy
   * @param var
   * @return GeodesicPursuit */
  public static GeodesicPursuitInterface fromTrajectory(GeodesicInterface geodesicInterface, Tensor tensor, TrajectoryEntryFinder entryFinder, Scalar var) {
    Optional<Tensor> lookAhead = entryFinder.on(tensor).apply(var).point;
    return lookAhead.isPresent() //
        ? new GeodesicPursuit(geodesicInterface, lookAhead.get())
        : VoidPursuit.INSTANCE;
  }

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param tensor waypoints
   * @param entryFinder strategy
   * @return GeodesicPursuit */
  public static GeodesicPursuitInterface fromTrajectory(GeodesicInterface geodesicInterface, Tensor tensor, TrajectoryEntryFinder entryFinder) {
    Optional<Tensor> lookAhead = entryFinder.initial(tensor).point;
    return lookAhead.isPresent() //
        ? new GeodesicPursuit(geodesicInterface, lookAhead.get())
        : VoidPursuit.INSTANCE;
  }

  public static GeodesicPursuitInterface fromTrajectory(Tensor tensor, TrajectoryEntryFinder entryFinder, Scalar var) {
    Optional<Tensor> lookAhead = entryFinder.on(tensor).apply(var).point;
    return lookAhead.isPresent() //
        ? new GeodesicPursuit(lookAhead.get())
        : VoidPursuit.INSTANCE;
  }

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param tensor waypoints
   * @param entryFinder strategy
   * @return GeodesicPursuit */
  public static GeodesicPursuitInterface fromTrajectory(Tensor tensor, TrajectoryEntryFinder entryFinder) {
    Optional<Tensor> lookAhead = entryFinder.initial(tensor).point;
    return lookAhead.isPresent() //
        ? new GeodesicPursuit(lookAhead.get())
        : VoidPursuit.INSTANCE;
  }
}
