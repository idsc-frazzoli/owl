// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnHermite1SubdivisionsTest extends TestCase {
  public void testSimple() {
    RnHermite1Subdivision hs = RnHermite1Subdivisions.of(RationalScalar.of(-1, 8), RationalScalar.of(-1, 2));
    assertEquals(hs.AMP, Tensors.fromString("{{1/2, +1/8}, {-3/4, -1/8}}"));
    assertEquals(hs.AMQ, Tensors.fromString("{{1/2, -1/8}, {+3/4, -1/8}}"));
  }
}
