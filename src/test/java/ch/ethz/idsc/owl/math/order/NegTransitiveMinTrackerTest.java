// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.owl.demo.order.TensorNormTotalPreorder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class NegTransitiveMinTrackerTest extends TestCase {
  public void testDigestNotEmpty() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(Norm.INFINITY);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    NegTransitiveMinTracker<Tensor> weakOrderMinTracker = NegTransitiveMinTracker.withList(weakOrderComparator);
    weakOrderMinTracker.digest(RealScalar.of(6));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 1);
  }

  public void testDigestFunction() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(Norm.INFINITY);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    NegTransitiveMinTracker<Tensor> weakOrderMinTracker = NegTransitiveMinTracker.withList(weakOrderComparator);
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
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(Norm.INFINITY);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    NegTransitiveMinTracker<Tensor> weakOrderMinTracker = NegTransitiveMinTracker.withList(weakOrderComparator);
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(weakOrderMinTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 1);
    weakOrderMinTracker.digest(Tensors.vector(0, 2, 1));
    assertTrue(weakOrderMinTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    weakOrderMinTracker.digest(Tensors.vector(0, 2, 1));
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(weakOrderMinTracker.getMinElements().contains(Tensors.vector(0, 2, 1)));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 2);
  }

  public void testWithSet() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(Norm.INFINITY);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    NegTransitiveMinTracker<Tensor> weakOrderMinTracker = NegTransitiveMinTracker.withSet(weakOrderComparator);
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    weakOrderMinTracker.digest(Tensors.vector(0, 1, 2));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 1);
    weakOrderMinTracker.digest(Tensors.vector(0, 2, 1));
    assertEquals(weakOrderMinTracker.getMinElements().size(), 2);
  }
}
