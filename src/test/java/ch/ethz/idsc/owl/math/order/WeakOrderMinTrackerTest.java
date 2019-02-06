// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class WeakOrderMinTrackerTest extends TestCase {
  public void testDigestNotEmpty() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparator<Tensor> comparator = tensorNormWeakOrder.comparator();
    WeakOrderMinTracker<Tensor> tensorNorm = new WeakOrderMinTracker<>(comparator);
    tensorNorm.digest(RealScalar.of(6));
    assertFalse(tensorNorm.getMinElements().isEmpty());
  }

  public void testDigestFunction() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparator<Tensor> comparator = tensorNormWeakOrder.comparator();
    WeakOrderMinTracker<Tensor> tensorNorm = new WeakOrderMinTracker<>(comparator);
    tensorNorm.digest(Tensors.vector(2));
    tensorNorm.digest(Tensors.vector(0, 2, 2));
    tensorNorm.digest(Tensors.vector(0, 1, 2));
    tensorNorm.digest(Tensors.vector(0, 3, 2));
    assertTrue(tensorNorm.getMinElements().contains(Tensors.vector(2)));
    assertTrue(tensorNorm.getMinElements().contains(Tensors.vector(0, 2, 2)));
    assertTrue(tensorNorm.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertFalse(tensorNorm.getMinElements().contains(Tensors.vector(0, 3, 2)));
  }

  public void testDuplicateEntries() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparator<Tensor> comparator = tensorNormWeakOrder.comparator();
    WeakOrderMinTracker<Tensor> tensorNorm = new WeakOrderMinTracker<>(comparator);
    tensorNorm.digest(Tensors.vector(0, 1, 2));
    tensorNorm.digest(Tensors.vector(0, 1, 2));
    assertEquals(tensorNorm.getMinElements().size(), 1);
  }
}
