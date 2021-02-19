// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class NaiveEntryFinderTest extends TestCase {
  private static final int DEPTH = 5;

  public void testNaive() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = NaiveEntryFinder.INSTANCE;
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Vector2Norm.of(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
    assertEquals(Tensors.vector(1, 0, 0), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
  }
}
