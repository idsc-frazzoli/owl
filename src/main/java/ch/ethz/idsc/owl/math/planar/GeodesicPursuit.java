// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.Nest;

public class GeodesicPursuit {
  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param tensor waypoints
   * @param entryFinder strategy
   * @param var
   * @return GeodesicPursuit */
  public static GeodesicPursuit fromTrajectory(GeodesicInterface geodesicInterface, Tensor tensor, TrajectoryEntryFinder entryFinder, Scalar var) {
    Optional<Tensor> lookAhead = entryFinder.on(tensor).apply(var);
    return new GeodesicPursuit(geodesicInterface, lookAhead);
  }

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param tensor waypoints
   * @param entryFinder strategy
   * @return GeodesicPursuit */
  public static GeodesicPursuit fromTrajectory(GeodesicInterface geodesicInterface, Tensor tensor, TrajectoryEntryFinder entryFinder) {
    Optional<Tensor> lookAhead = entryFinder.initial(tensor);
    return new GeodesicPursuit(geodesicInterface, lookAhead);
  }

  // ---
  private final Tensor ratios;
  private Tensor curve = null;

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param lookAhead trajectory point {px, py, pa} */
  public GeodesicPursuit(GeodesicInterface geodesicInterface, Optional<Tensor> lookAhead) {
    // TODO play with/parameterize degree [1, 3, ...] and refinement [4, 5, ...]
    LaneRiesenfeldCurveSubdivision laneRiesenfeldCurveSubdivision = new LaneRiesenfeldCurveSubdivision(geodesicInterface, 1);
    ratios = lookAhead.map(vector -> VectorQ.requireLength(vector, 3)) //
        .map(vector -> Tensors.of(Array.zeros(3), vector)).map(tensor -> {
          curve = Nest.of(laneRiesenfeldCurveSubdivision::string, tensor, 5);
          Tensor points2D = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
          return SignedCurvature2D.string(points2D);
        }).orElse(null);
  }

  /** @return Tensor of planned geodesic curve trajectory */
  public Optional<Tensor> curve() {
    return Optional.of(curve);
  }

  /** @return Tensor of turning ratios required to drive the calculated geodesic curve */
  public Optional<Tensor> ratios() {
    return Optional.ofNullable(ratios);
  }

  /** @return first/current turning ratio required to drive the calculated geodesic curve */
  public Optional<Scalar> ratio() {
    return ratios().map(vector -> vector.Get(0));
  }
}
