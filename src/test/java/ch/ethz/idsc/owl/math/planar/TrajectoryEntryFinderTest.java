// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.planar.ClothoidDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class TrajectoryEntryFinderTest extends TestCase {
  private static final Tensor WAYPOINTS = Tensors.of( //
      Tensors.vector(0, 0, 0), //
      Tensors.vector(1, 0.5, Math.PI / 4), //
      Tensors.vector(2, 1, 0), //
      Tensors.vector(4, 1, 0));

  public void testNaive() {
    TrajectoryEntryFinder finder = NaiveEntryFinder.INSTANCE;
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(0.3)).point;
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(0, 0, 0), waypoint.get());
  }

  public void testInterpolation() {
    TrajectoryEntryFinder finder = InterpolationEntryFinder.INSTANCE;
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(2.5)).point;
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(3, 1, 0), waypoint.get());
  }

  public void testIntersection() {
    Tensor goalSE2 = Tensors.vector(3, 1, 0);
    TrajectoryEntryFinder finder = IntersectionEntryFinder.INSTANCE;
    // 2D
    Optional<Tensor> waypointSE2 = finder.on(WAYPOINTS).apply(ClothoidDistance.INSTANCE.norm(goalSE2)).point;
    assertTrue(waypointSE2.isPresent());
    Chop._01.requireClose(goalSE2, waypointSE2.get());
    // SE2
    Tensor goal2D = Extract2D.FUNCTION.apply(goalSE2);
    Tensor waypoints = Tensor.of(WAYPOINTS.stream().map(Extract2D.FUNCTION));
    Optional<Tensor> waypoint2D = finder.on(waypoints).apply(Norm._2.of(goal2D)).point;
    assertTrue(waypoint2D.isPresent());
    Chop._01.requireClose(goal2D, waypoint2D.get());
  }

  public void testGeodesic() {
    TrajectoryEntryFinder finder = new GeodesicInterpolationEntryFinder(ClothoidCurve.INSTANCE);
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(2.5)).point;
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(3, 1, 0), waypoint.get());
  }

  // ---
  private static int INIT = 0;

  private static ScalarUnaryOperator func(Tensor tensor) {
    ++INIT;
    return s -> s;
  }

  public void testOnce() {
    long count = IntStream.range(0, 10).mapToObj(RealScalar::of).map(func(null)).count();
    assertEquals(count, 10);
    assertEquals(INIT, 1);
  }
}
