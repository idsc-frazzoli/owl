// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class TrajectoryEntryFinderTest extends TestCase {
  /* package */ static final Tensor WAYPOINTS = Tensors.of( //
      Tensors.vector(0, 0, 0), //
      Tensors.vector(1, 0.5, Math.PI / 4), //
      Tensors.vector(2, 1, 0), //
      Tensors.vector(4, 1, 0));

  public void testNaive() {
    TrajectoryEntryFinder finder = NaiveEntryFinder.INSTANCE;
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(0.3)).point();
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(0, 0, 0), waypoint.get());
  }

  public void testInterpolation() {
    TrajectoryEntryFinder finder = InterpolationEntryFinder.INSTANCE;
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(2.5)).point();
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(3, 1, 0), waypoint.get());
  }

  public void testGeodesic() {
    TrajectoryEntryFinder finder = new GeodesicInterpolationEntryFinder(Se2Clothoids.INSTANCE);
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(2.5)).point();
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
