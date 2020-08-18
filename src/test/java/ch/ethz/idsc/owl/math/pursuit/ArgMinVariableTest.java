// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Arrays;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.clt.Se2ClothoidBuilder;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class ArgMinVariableTest extends TestCase {
  private static final int DEPTH = 5;

  public void testInterpolation() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}");
    TrajectoryEntryFinder entryFinder = InterpolationEntryFinder.INSTANCE;
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point().get().map(N.DOUBLE).map(Chop._06));
  }

  public void testIntersection() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = IntersectionEntryFinder.SPHERE_SE2;
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
    assertEquals(Tensors.vector(1, 0, 0), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
  }

  public void testGeodesic() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = new GeodesicInterpolationEntryFinder(Se2ClothoidBuilder.INSTANCE);
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
  }

  public void testPerformance() {
    Tensor timings = Tensors.empty();
    Tensor tensor = Tensor.of(IntStream.range(-100, 100).mapToObj(RealScalar::of).map(Tensors.vector(1, 2, 0)::multiply)).unmodifiable();
    assertEquals(Dimensions.of(tensor), Arrays.asList(200, 3));
    TrajectoryEntryFinder entryFinder = IntersectionEntryFinder.SPHERE_SE2;
    for (int i = 0; i < 20; i++) {
      Timing timing = Timing.started();
      Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
      timings.append(RealScalar.of(timing.seconds()));
      assertEquals(Dimensions.of(tensor), Arrays.asList(200, 3));
      assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
    }
    Scalar mean_duration = N.DOUBLE.of(Mean.of(timings).Get()); // in seconds
    assertTrue(Scalars.lessEquals(mean_duration, RealScalar.of(2.3))); // datahaki -> 0.20723770650000004
  }
}
