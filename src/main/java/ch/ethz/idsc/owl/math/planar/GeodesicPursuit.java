// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

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
  private final GeodesicInterface geodesicInterface;
  private final Optional<Tensor> lookAhead;
  private final Optional<Scalar> ratio;
  // ---
  private final Tensor discretization = Subdivide.of(0, 1, 100); // FIXME JG pass 100 as argument

  /** @param geodesicInterface type of curve to connect points {px, py, pa}
   * @param lookAhead trajectory point {px, py, pa} */
  public GeodesicPursuit(GeodesicInterface geodesicInterface, Optional<Tensor> lookAhead) {
    this.geodesicInterface = geodesicInterface;
    this.lookAhead = lookAhead;
    ratio = lookAhead.isPresent() //
        ? ratio(lookAhead.get()) //
        : Optional.empty();
  }

  /** @param lookAhead trajectory point {px, py, pa}
   * @return ratio */
  private Optional<Scalar> ratio(Tensor lookAhead) {
    ScalarTensorFunction geodesic = geodesicInterface.curve(Array.zeros(3), lookAhead);
    Tensor curve = discretization.map(geodesic);
    Tensor points2D = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
    Tensor curvature = SignedCurvature2D.string(points2D);
    return Optional.of(curvature.Get(0));
  }

  public Optional<Tensor> lookAhead() {
    return lookAhead;
  }

  public Optional<Scalar> ratio() {
    return ratio;
  }
}
