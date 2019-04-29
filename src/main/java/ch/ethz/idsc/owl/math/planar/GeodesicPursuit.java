// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.Nest;

public class GeodesicPursuit implements GeodesicPursuitInterface {
  private final static int DEGREE = 1;
  private final static int REFINEMENT = 5;

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

  // ---
  private final Tensor ratios;
  private final Tensor curve;

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param lookAhead trajectory point {px, py, pa} */
  public GeodesicPursuit(GeodesicInterface geodesicInterface, Tensor lookAhead) {
    VectorQ.requireLength(lookAhead, 3);
    LaneRiesenfeldCurveSubdivision laneRiesenfeldCurveSubdivision = new LaneRiesenfeldCurveSubdivision(geodesicInterface, DEGREE);
    curve = Nest.of(laneRiesenfeldCurveSubdivision::string, Tensors.of(lookAhead.map(Scalar::zero), lookAhead), REFINEMENT);
    ratios = SignedCurvature2D.string(Tensor.of(curve.stream().map(Extract2D.FUNCTION)));
  }

  @Override // from GeodesicPursuitInterface
  public Tensor curve() {
    return curve;
  }

  @Override // from GeodesicPursuitInterface
  public Tensor ratios() {
    return ratios;
  }

  @Override // from GeodesicPursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.of(ratios.Get(0));
  }
}
