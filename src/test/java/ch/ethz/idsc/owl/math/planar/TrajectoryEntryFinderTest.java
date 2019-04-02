// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class TrajectoryEntryFinderTest extends TestCase {
  private Optional<Tensor> noWaypoints = Optional.empty();
  private Optional<Tensor> waypoints = Optional.of(Tensors.of( //
      Tensors.vector(0, 0, 0), //
      Tensors.vector(1, 0.5, Math.PI / 4), //
      Tensors.vector(2, 1, 0), //
      Tensors.vector(4, 1, 0)));

  public void testNaive() {
    TrajectoryEntryFinder finder = new NaiveEntryFinder(0);
    // ---
    assertFalse(finder.apply(noWaypoints).isPresent());
    // ---
    Optional<Tensor> waypoint = finder.apply(waypoints);
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(0, 0, 0), waypoint.get());
  }

  public void testInterpolation() {
    TrajectoryEntryFinder finder = new InterpolationEntryFinder(2.5);
    // ---
    assertFalse(finder.apply(noWaypoints).isPresent());
    // ---
    Optional<Tensor> waypoint = finder.apply(waypoints);
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(3, 1, 0), waypoint.get());
  }

  public void testIntersection() {
    Tensor goal = Tensors.vector(3, 1);
    TrajectoryEntryFinder finder = new IntersectionEntryFinder(Norm._2.of(goal));
    // ---
    assertFalse(finder.apply(noWaypoints).isPresent());
    // ---
    Optional<Tensor> waypoint = finder.apply(waypoints);
    assertTrue(waypoint.isPresent());
    assertTrue(Scalars.lessThan( //
        Norm._2.between(goal, waypoint.get()), //
        Norm._2.of(goal).multiply(RationalScalar.of(1, 100))));
  }

  public void testGeodesic() {
    TrajectoryEntryFinder finder = new GeodesicInterpolationEntryFinder(ClothoidCurve.INSTANCE, 2.5);
    // ---
    assertFalse(finder.apply(noWaypoints).isPresent());
    // ---
    Optional<Tensor> waypoint = finder.apply(waypoints);
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(3, 1, 0), waypoint.get());
  }
}
