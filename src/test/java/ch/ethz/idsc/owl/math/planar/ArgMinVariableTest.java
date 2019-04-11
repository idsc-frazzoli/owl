// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ArgMinVariableTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}");
    TrajectoryEntryFinder entryFinder = new InterpolationEntryFinder(0);
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Norm._2.ofVector(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).get().map(Chop._06));
  }
}
