// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class TensorNormWeakOrderTest extends TestCase {
  public void testSimple() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    assertEquals(weakOrderComparator.compare(Tensors.vector(1, 3), Tensors.vector(3, 12)), WeakOrderComparison.LESS_EQUALS_ONLY);
    assertEquals(weakOrderComparator.compare(Tensors.vector(12, 3), Tensors.vector(3, 12)), WeakOrderComparison.INDIFFERENT);
    assertEquals(weakOrderComparator.compare(Tensors.vector(3, 12), Tensors.vector(3, 12)), WeakOrderComparison.INDIFFERENT);
    assertEquals(weakOrderComparator.compare(Tensors.vector(100, 3), Tensors.vector(3, 12)), WeakOrderComparison.GREATER_EQUALS_ONLY);
  }

  public void testVector() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparison weakOrderComparison = tensorNormWeakOrder.comparator().compare(Tensors.vector(0, 1, 2), Tensors.vector(0, 2, 1));
    System.out.println(Norm.INFINITY.of(Tensors.vector(0, 1, 2)));
    assertEquals(weakOrderComparison, WeakOrderComparison.INDIFFERENT);
  }

  public void testMatrix() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    Tensor m1 = Tensors.fromString("{{1,2},{2,3}}");
    Tensor m2 = Tensors.fromString("{{2,1},{2,3}}");
    Tensor m3 = Tensors.fromString("{{1,1},{2,3}}");
    Tensor m4 = Tensors.fromString("{{1,1},{1,3}}");
    System.out.println(Pretty.of(m1));
    System.out.println(Norm.INFINITY.of(m1));
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m2), WeakOrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m3), WeakOrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m2, m3), WeakOrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m4), WeakOrderComparison.GREATER_EQUALS_ONLY);
    assertEquals(tensorNormWeakOrder.comparator().compare(m2, m4), WeakOrderComparison.GREATER_EQUALS_ONLY);
    assertEquals(tensorNormWeakOrder.comparator().compare(m3, m4), WeakOrderComparison.GREATER_EQUALS_ONLY);
  }
}
