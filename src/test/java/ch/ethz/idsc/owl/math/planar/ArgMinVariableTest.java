// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ArgMinVariableTest extends TestCase {
  public void testNaive() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}");
    TrajectoryEntryFinder entryFinder = new NaiveEntryFinder(0);
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Tensors.vector(1, 0, 0), entryFinder.on(tensor).apply(var).point.get().map(Chop._06));
  }

  public void testInterpolation() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}");
    TrajectoryEntryFinder entryFinder = new InterpolationEntryFinder(0);
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point.get().map(Chop._06));
  }

  public void testIntersection() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}");
    TrajectoryEntryFinder entryFinder = new IntersectionEntryFinder(RealScalar.of(3));
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Tensors.vector(1, 0), entryFinder.on(tensor).apply(var).point.get().map(Chop._06));
  }

  public void testGeodesic() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}");
    TrajectoryEntryFinder entryFinder = new GeodesicInterpolationEntryFinder(0, ClothoidCurve.INSTANCE);
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point.get().map(Chop._06));
  }
}
