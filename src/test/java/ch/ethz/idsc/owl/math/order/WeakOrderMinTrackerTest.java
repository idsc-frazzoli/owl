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
    WeakOrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    WeakOrderMinTracker<Tensor> weakOrderMinTracker = new WeakOrderMinTracker<>(weakOrderComparator);
    weakOrderMinTracker.digest(RealScalar.of(6));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 1);
  }

  public void testDigestFunction() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    WeakOrderMinTracker<Tensor> weakOrderMinTracker = new WeakOrderMinTracker<>(weakOrderComparator);
    weakOrderMinTracker.digest(Tensors.vector(2));
    weakOrderMinTracker.digest(Tensors.vector(0, 3, 2));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 1);
    assertTrue(weakOrderMinTracker.getMinElements().contains(Tensors.vector(2)));
    weakOrderMinTracker.digest(Tensors.vector(0, 2, 2));
    assertTrue(weakOrderMinTracker.getMinElements().contains(Tensors.vector(0, 2, 2)));
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(weakOrderMinTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    weakOrderMinTracker.digest(Tensors.vector(0, 3, 2));
    assertTrue(weakOrderMinTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertFalse(weakOrderMinTracker.getMinElements().contains(Tensors.vector(0, 3, 2)));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 3);
  }

  public void testDuplicateEntries() {
    TensorNormWeakOrder tensorNormWeakOrder = new TensorNormWeakOrder(Norm.INFINITY);
    WeakOrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    WeakOrderMinTracker<Tensor> weakOrderMinTracker = new WeakOrderMinTracker<>(weakOrderComparator);
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 1);
    weakOrderMinTracker.digest(Tensors.vector(0, 2, 1));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 2);
  }
}
