// code by jph, gjoel
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class SubdivisionClothoidDistanceTest extends TestCase {
  public void testSimple() {
    Scalar scalar = SubdivisionClothoidDistance.INSTANCE.norm(Tensors.fromString("{1[m], 2[m], 0.3}"));
    Clips.interval(Quantity.of(2.4, "m"), Quantity.of(2.5, "m")).requireInside(scalar);
  }

  public void testOrigin() {
    assertEquals(SubdivisionClothoidDistance.INSTANCE.norm(Tensors.vector(0, 0, 0)), RealScalar.of(0));
  }
  /** valid if {@link PseudoClothoidDistance} also depends on {@link Clothoid3} */
  // public void testComparison() {
  // Distribution distribution = NormalDistribution.standard();
  // for (int count = 0; count < 1000; ++count) {
  // Tensor p = RandomVariate.of(distribution, 3);
  // Tensor q = RandomVariate.of(distribution, 3);
  // Tensor d1 = PseudoClothoidDistance.INSTANCE.distance(p, q);
  // Tensor d2 = SubdivisionClothoidDistance.INSTANCE.distance(p, q);
  // Chop._02.requireClose(d1, d2);
  // }
  // }
}
