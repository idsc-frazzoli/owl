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
  public static GeodesicPursuit fromTrajectory(GeodesicInterface geodesicInterface, Tensor tensor, TrajectoryEntryFinder entryFinder, Scalar var, int resolution) {
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
  private final GeodesicInterface geodesicInterface;
  private final Tensor discretization;
  private final Optional<Tensor> lookAhead;
  private final Optional<Tensor> ratios;

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param lookAhead trajectory point {px, py, pa}
   * @param resolution of geodesic curve */
  public GeodesicPursuit(GeodesicInterface geodesicInterface, Optional<Tensor> lookAhead, int resolution) {
    this.geodesicInterface = geodesicInterface;
    discretization = Subdivide.of(0, 1, 100);
    this.lookAhead = lookAhead;
    ratios = lookAhead.isPresent() //
        ? ratios(VectorQ.requireLength(lookAhead.get(), 3)) //
        : Optional.empty();
  }

  /** @param lookAhead trajectory point {px, py, pa}
   * @return ratios */
  private Optional<Tensor> ratios(Tensor lookAhead) {
    ScalarTensorFunction geodesic = geodesicInterface.curve(Array.zeros(3), lookAhead);
    Tensor curve = discretization.map(geodesic);
    Tensor points2D = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
    Tensor curvature = SignedCurvature2D.string(points2D).negate(); // SignedCurvature2D uses a switched sign convention
    return Optional.of(curvature);
  }

  public Optional<Tensor> lookAhead() {
    return lookAhead;
  }

  public Optional<Tensor> ratios() {
    return ratios;
  }

  public Optional<Scalar> ratio() {
    if (ratios.isPresent())
      return Optional.of(ratios.get().Get(0));
    return Optional.empty();
  }
}
