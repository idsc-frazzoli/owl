// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import junit.framework.TestCase;

public class LoewnerPartialOrderTest extends TestCase {
  public void testDyn() {
    Tensor x = DiagonalMatrix.of(1, 2);
    Tensor y = DiagonalMatrix.of(2, 1);
    OrderComparison orderComparison = LoewnerPartialOrder.INSTANCE.compare(x, y);
    assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
  }

  public void testDyn2() {
    Tensor x = DiagonalMatrix.of(1, 1);
    Tensor y = DiagonalMatrix.of(2, 2);
    OrderComparison orderComparison = LoewnerPartialOrder.INSTANCE.compare(x, y);
    assertEquals(orderComparison, OrderComparison.STRICTLY_SUCCEEDS);
  }
}
