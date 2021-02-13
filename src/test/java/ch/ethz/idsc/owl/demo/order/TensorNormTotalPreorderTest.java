// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.owl.math.order.OrderComparison;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.VectorNormInfinity;
import junit.framework.TestCase;

public class TensorNormTotalPreorderTest extends TestCase {
  public void testSimple() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorNormInfinity::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    assertEquals(weakOrderComparator.compare(Tensors.vector(1, 3), Tensors.vector(3, 12)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(weakOrderComparator.compare(Tensors.vector(12, 3), Tensors.vector(3, 12)), OrderComparison.INDIFFERENT);
    assertEquals(weakOrderComparator.compare(Tensors.vector(3, 12), Tensors.vector(3, 12)), OrderComparison.INDIFFERENT);
    assertEquals(weakOrderComparator.compare(Tensors.vector(100, 3), Tensors.vector(3, 12)), OrderComparison.STRICTLY_SUCCEEDS);
  }

  public void testVector() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorNormInfinity::of);
    OrderComparison weakOrderComparison = tensorNormWeakOrder.comparator().compare(Tensors.vector(0, 1, 2), Tensors.vector(0, 2, 1));
    assertEquals(weakOrderComparison, OrderComparison.INDIFFERENT);
  }

  public void testMatrix() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorNormInfinity::of);
    Tensor m1 = Tensors.fromString("{{1, 2}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{2, 1}, {2, 3}}");
    Tensor m3 = Tensors.fromString("{{1, 1}, {2, 3}}");
    Tensor m4 = Tensors.fromString("{{1, 1}, {1, 3}}");
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m2), OrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m3), OrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m2, m3), OrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m4), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(tensorNormWeakOrder.comparator().compare(m2, m4), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(tensorNormWeakOrder.comparator().compare(m3, m4), OrderComparison.STRICTLY_SUCCEEDS);
  }
}
