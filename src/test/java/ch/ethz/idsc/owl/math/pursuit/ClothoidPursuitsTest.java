// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class ClothoidPursuitsTest extends TestCase {
  public void testCurve() {
    for (int depth = 0; depth < 5; ++depth) {
      Tensor tensor = ClothoidPursuits.curve(Tensors.fromString("{10, 1, 1}"), depth);
      assertEquals(tensor.length(), (1 << depth) + 1);
    }
  }

  public void testFromTrajectory() {
    PursuitInterface geodesicPursuit;
    Tensor trajectory = Tensors.of( //
        Tensors.vector(0, 0, 0), //
        Tensors.vector(2, 2, Math.PI / 2), //
        Tensors.vector(4, 4, Math.PI / 2));
    // ---
    geodesicPursuit = ClothoidPursuits.fromTrajectory(trajectory, NaiveEntryFinder.INSTANCE, RealScalar.ONE);
    // System.out.println("ratios 1 = " + (geodesicPursuit.firstRatio().isPresent() ? geodesicPursuit.firstRatio().get() : "empty"));
    assertEquals(RationalScalar.of(1, 2), Round._8.apply(geodesicPursuit.firstRatio().orElse(null)));
  }
}
