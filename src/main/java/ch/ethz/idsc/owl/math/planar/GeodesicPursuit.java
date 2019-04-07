// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class GeodesicPursuit {
  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param tensor waypoints
   * @param entryFinder strategy
   * @param var
   * @param resolution of geodesic curve
   * @return GeodesicPursuit */
  public static GeodesicPursuit fromTrajectory(GeodesicInterface geodesicInterface, Tensor tensor, TrajectoryEntryFinder entryFinder, Scalar var,
      int resolution) {
    Optional<Tensor> lookAhead = entryFinder.on(tensor).apply(var);
    return new GeodesicPursuit(geodesicInterface, lookAhead, resolution);
  }

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param tensor waypoints
   * @param entryFinder strategy
   * @param resolution of geodesic curve
   * @return GeodesicPursuit */
  public static GeodesicPursuit fromTrajectory(GeodesicInterface geodesicInterface, Tensor tensor, TrajectoryEntryFinder entryFinder, int resolution) {
    Optional<Tensor> lookAhead = entryFinder.initial(tensor);
    return new GeodesicPursuit(geodesicInterface, lookAhead, resolution);
  }

  // ---
  private final Tensor ratios;
  private Tensor curve = null;

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param lookAhead trajectory point {px, py, pa}
   * @param resolution of geodesic curve */
  public GeodesicPursuit(GeodesicInterface geodesicInterface, Optional<Tensor> lookAhead, int resolution) {
    Tensor discretization = Subdivide.of(0, 1, resolution);
    ratios = lookAhead.map(vector -> VectorQ.requireLength(vector, 3)).map(vector -> {
      ScalarTensorFunction geodesic = geodesicInterface.curve(Array.zeros(3), vector);
      curve = discretization.map(geodesic);
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
