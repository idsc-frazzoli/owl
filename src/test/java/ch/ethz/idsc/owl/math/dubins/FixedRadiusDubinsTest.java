// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class FixedRadiusDubinsTest extends TestCase {
  public void testTest() {
    FixedRadiusDubins fixedRadiusDubins = new FixedRadiusDubins(Tensors.vector(10, 2, Math.PI / 2), RealScalar.of(1));
    assertEquals(fixedRadiusDubins.allValid().count(), 4);
  }
}
