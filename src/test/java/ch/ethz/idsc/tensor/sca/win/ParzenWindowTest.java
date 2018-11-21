// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ParzenWindowTest extends TestCase {
  public void testSimple() {
    assertEquals(ParzenWindow.FUNCTION.apply(RationalScalar.of(1, 10)), RationalScalar.of(101, 125));
    assertEquals(ParzenWindow.FUNCTION.apply(RationalScalar.of(3, 10)), RationalScalar.of(16, 125));
  }

  public void testQuantityFail() {
    try {
      ParzenWindow.FUNCTION.apply(Quantity.of(2, "s"));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
