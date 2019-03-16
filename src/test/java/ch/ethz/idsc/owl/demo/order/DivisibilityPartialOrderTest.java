// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.demo.order.DivisibilityPreorder;
import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class DivisibilityPartialOrderTest extends TestCase {
  public void testSimple() {
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(1), RealScalar.of(6)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(6)), OrderComparison.INDIFFERENT);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(2)), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(7)), OrderComparison.INCOMPARABLE);
  }
}
