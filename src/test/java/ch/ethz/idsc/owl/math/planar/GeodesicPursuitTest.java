// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class GeodesicPursuitTest extends TestCase {
  public void test() {
    GeodesicPursuit geodesicPursuit;
    Tensor trajectory1 = Tensors.of( //
        Tensors.vector(2, 0, 0), //
        Tensors.vector(4, 2, Math.PI / 2), //
        Tensors.vector(4, 4, Math.PI / 2));
    Tensor trajectory2 = Tensors.of( //
        Tensors.vector(2, 2, Math.PI / 2), //
        Tensors.vector(4, 4, Math.PI / 2));
    // ---
    geodesicPursuit = GeodesicPursuit.fromTrajectory(ClothoidCurve.INSTANCE, trajectory1, new NaiveEntryFinder(0));
    // System.out.println("ratios 1 = " + (ratio1.isPresent() ? ratio1.get() : "empty"));
    assertEquals(RealScalar.ZERO, geodesicPursuit.ratio().orElse(null));
    // ---
    geodesicPursuit = GeodesicPursuit.fromTrajectory(ClothoidCurve.INSTANCE, trajectory2, new NaiveEntryFinder(0));
    // System.out.println("ratios 2 = " + (ratio2.isPresent() ? ratio2.get() : "empty"));
    if (geodesicPursuit.ratios().isPresent())
      assertEquals(RationalScalar.of(-1, 2), Round._8.apply(geodesicPursuit.ratio().get()));
    else
      assert false;
  }
}
