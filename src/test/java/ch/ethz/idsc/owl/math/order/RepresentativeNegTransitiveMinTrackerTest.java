// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.IOException;

import ch.ethz.idsc.owl.demo.order.ClipStrictPartialOrder;
import ch.ethz.idsc.owl.demo.order.TensorNormTotalPreorder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.nrm.VectorInfinityNorm;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class RepresentativeNegTransitiveMinTrackerTest extends TestCase {
  public void testDigestNotEmpty() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(RealScalar.of(6));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  public void testDigestFunction() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(Tensors.vector(2));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(2)));
    minTracker.digest(Tensors.vector(0, 2, 2));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 2, 2)));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 3, 2)));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  public void testDuplicateEntries() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 4, 1));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 1, 1));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 1)));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  public void testWithSet() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withSet(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 0, 2));
    minTracker.digest(Tensors.vector(0, 0, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 2, 0));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = Serialization.copy(RepresentativeNegTransitiveMinTracker.withSet(weakOrderComparator));
    minTracker.digest(Tensors.vector(0, 1, 2));
  }

  public void testIncomparability() {
    OrderComparator<Clip> intervalOrder = ClipStrictPartialOrder.INSTANCE;
    MinTracker<Clip> minTracker = RepresentativeNegTransitiveMinTracker.withList(intervalOrder);
    minTracker.digest(Clips.interval(0, 1));
    minTracker.digest(Clips.interval(0, 1));
    assertEquals(minTracker.getMinElements().size(), 1);
  }
}
