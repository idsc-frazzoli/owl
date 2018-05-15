// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RadiusXYTest extends TestCase {
  public void testSimple() {
    assertEquals(RadiusXY.requireSame(Tensors.vector(2, 2, 3)), RealScalar.of(2));
  }

  public void testFail() {
    try {
      RadiusXY.requireSame(Tensors.vector(1, 2));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
