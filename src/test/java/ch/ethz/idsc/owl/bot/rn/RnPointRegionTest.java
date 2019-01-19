// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnPointRegionTest extends TestCase {
  public void testSimple() {
    RnPointRegion pointRegion = new RnPointRegion(Tensors.vector(1, 2, 3, 4));
    Scalar scalar = pointRegion.distance(Tensors.vector(2, 3, 2, 5));
    assertEquals(scalar, RealScalar.of(2));
    ExactScalarQ.require(scalar);
    assertFalse(pointRegion.isMember(Tensors.vector(2, 3, 4, 5)));
    assertTrue(pointRegion.isMember(Tensors.vector(1, 2, 3, 4)));
  }
}
