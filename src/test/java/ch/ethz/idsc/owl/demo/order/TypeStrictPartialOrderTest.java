// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.util.List;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class TypeStrictPartialOrderTest extends TestCase {
  public void testSimple() {
    assertFalse(Scalar.class.isAssignableFrom(Tensor.class));
    assertTrue(Scalar.class.isAssignableFrom(Scalar.class));
    assertTrue(Tensor.class.isAssignableFrom(Scalar.class));
  }

  public void testCompare() {
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(Tensor.class, Scalar.class), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(Object.class, Scalar.class), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(Tensor.class, Object.class), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(List.class, Object.class), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(List.class, List.class), OrderComparison.INDIFFERENT);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(List.class, Tensor.class), OrderComparison.INCOMPARABLE);
  }
}
