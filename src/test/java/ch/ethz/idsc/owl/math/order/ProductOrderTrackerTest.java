// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.owl.demo.order.ScalarTotalOrder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class ProductOrderTrackerTest extends TestCase {
  public void testSimple() {
    ProductOrderTracker<Scalar> productOrderTracker = new ProductOrderTracker<>(ScalarTotalOrder.INSTANCE);
    assertEquals(productOrderTracker.digest(RealScalar.of(0), RealScalar.of(0)), OrderComparison.INDIFFERENT);
    assertEquals(productOrderTracker.digest(RealScalar.of(0), RealScalar.of(1)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderTracker.digest(RealScalar.of(0), RealScalar.of(0)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderTracker.digest(RealScalar.of(1), RealScalar.of(1)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderTracker.digest(RealScalar.of(1), RealScalar.of(0)), OrderComparison.INCOMPARABLE);
  }
}
