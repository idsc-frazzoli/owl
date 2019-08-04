// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class DivisibilityPreorderTest extends TestCase {
  public void testSimple() {
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(1), RealScalar.of(6)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(6)), OrderComparison.INDIFFERENT);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(2)), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(7)), OrderComparison.INCOMPARABLE);
  }

  public void testZeroFail() {
    try {
      DivisibilityPreorder.INSTANCE.compare(RealScalar.ZERO, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      DivisibilityPreorder.INSTANCE.compare(RealScalar.ONE, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
