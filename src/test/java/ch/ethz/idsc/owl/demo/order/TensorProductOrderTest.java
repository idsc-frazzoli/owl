// code by astoll
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.owl.math.order.ProductOrderComparator;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TensorProductOrderTest extends TestCase {
  public void testSimple() {
    ProductOrderComparator productOrderComparator = TensorProductOrder.comparator(3);
    Tensor tensorX = Tensors.fromString("{1,2,3}");
    Tensor tensorY = Tensors.fromString("{2,3,4}");
    Tensor tensorZ = Tensors.fromString("{0,3,4}");
    assertEquals(productOrderComparator.compare(tensorX, tensorY), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderComparator.compare(tensorX, tensorZ), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(tensorY, tensorX), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(productOrderComparator.compare(tensorX, tensorX), OrderComparison.INDIFFERENT);
  }

  public void testDimOne() {
    ProductOrderComparator productOrderComparator = TensorProductOrder.comparator(2);
    Tensor tensorX = Tensors.fromString("{1,2,3}");
    Tensor tensorY = Tensors.fromString("{2,3,4}");
    OrderComparison orderComparison1 = productOrderComparator.compare(tensorX.extract(0, 2), tensorY.extract(0, 2));
    assertEquals(orderComparison1, OrderComparison.STRICTLY_PRECEDES);
  }
}
