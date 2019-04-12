// code by astoll
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TensorProductOrderTest extends TestCase {
  public void testSimple() {
    TensorProductOrder tensorProductOrder = TensorProductOrder.createTensorProductOrder(3);
    Tensor tensorX = Tensors.fromString("{1,2,3}");
    Tensor tensorY = Tensors.fromString("{2,3,4}");
    Tensor tensorZ = Tensors.fromString("{0,3,4}");
    OrderComparison orderComparison1 = tensorProductOrder.compare(tensorX, tensorY);
    OrderComparison orderComparison2 = tensorProductOrder.compare(tensorX, tensorZ);
    assertEquals(orderComparison1, OrderComparison.STRICTLY_PRECEDES);
    assertEquals(orderComparison2, OrderComparison.INCOMPARABLE);
  }

  public void testDimOne() {
    TensorProductOrder tensorProductOrder = TensorProductOrder.createTensorProductOrder(2);
    Tensor tensorX = Tensors.fromString("{1,2,3}");
    Tensor tensorY = Tensors.fromString("{2,3,4}");
    OrderComparison orderComparison1 = tensorProductOrder.compare(tensorX.extract(0, 2), tensorY.extract(0, 2));
    assertEquals(orderComparison1, OrderComparison.STRICTLY_PRECEDES);
  }
}
