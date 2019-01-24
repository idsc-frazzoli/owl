// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class TensorNormWeakOrderTest extends TestCase {
  public void testSimple() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparator<Tensor> WeakOrderComparator = tensorNormWeakOrder.comparator();
    assertEquals(WeakOrderComparator.compare(Tensors.vector(12, 3), Tensors.vector(3, 12)), WeakOrderComparison.INDIFFERENT);
    assertEquals(WeakOrderComparator.compare(Tensors.vector(1, 3), Tensors.vector(3, 12)), WeakOrderComparison.LESS_EQUALS_ONLY);
  }
}
