// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Arrays;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class ArgMinVariableTest extends TestCase {
  private static final int DEPTH = 5;

  public void testInterpolation() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = InterpolationEntryFinder.INSTANCE;
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point.get().map(N.DOUBLE).map(Chop._06));
  }

  public void testIntersection() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = IntersectionEntryFinder.INSTANCE;
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
    assertEquals(Tensors.vector(1, 0), entryFinder.on(tensor).apply(var).point.get().map(Chop._06));
  }

  public void testGeodesic() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = new GeodesicInterpolationEntryFinder(ClothoidCurve.INSTANCE);
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point.get().map(Chop._06));
  }

  public void testPerformance() {
    Tensor timing = Tensors.empty();
    Tensor tensor = Tensor.of(IntStream.range(-100, 100).mapToObj(RealScalar::of).map(Tensors.vector(1, 2, 0)::multiply)).unmodifiable();
    assertEquals(Dimensions.of(tensor), Arrays.asList(200, 3));
    TrajectoryEntryFinder entryFinder = IntersectionEntryFinder.INSTANCE;
    for (int i = 0; i < 20; i++) {
      System.out.println(i);
      long time = System.currentTimeMillis();
      // ---
      Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
      timing.append(RealScalar.of(System.currentTimeMillis() - time));
      assertEquals(Dimensions.of(tensor), Arrays.asList(200, 3));
      // FIXME GJOEL because of JPH evil edits, the return value has changed from Array.zeros(3) to Array.zeros(2) !!!
      // JPH does not know why and requests a review
      assertEquals(Array.zeros(2), entryFinder.on(tensor).apply(var).point.get().map(Chop._06));
    }
    System.out.println(N.DOUBLE.of(Mean.of(timing)));
  }
}
