// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.IOException;
import java.util.Collection;

import ch.ethz.idsc.owl.demo.order.TensorNormTotalPreorder;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.nrm.VectorInfinityNorm;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class NegTransitiveMinTrackerTest extends TestCase {
  public void testDigestNotEmpty() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(RealScalar.of(6));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  public void testDigestFunction() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(Tensors.vector(2));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(2)));
    minTracker.digest(Tensors.vector(0, 2, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 2, 2)));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 3, 2)));
    assertEquals(minTracker.getMinElements().size(), 3);
  }

  public void testDuplicateEntries() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 4, 1));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 2, 1));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    minTracker.digest(Tensors.vector(0, 2, 1));
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 3, 3));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 2, 1)));
    assertEquals(minTracker.getMinElements().size(), 2);
  }

  public void testWithSet() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.withSet(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 2, 1));
    assertEquals(minTracker.getMinElements().size(), 2);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = Serialization.copy(NegTransitiveMinTracker.withSet(weakOrderComparator));
    minTracker.digest(Tensors.vector(0, 1, 2));
  }

  public void testPermutations() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 10);
    final Tensor tensor = RandomVariate.of(distribution, 100, 3);
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    Collection<Tensor> collection1;
    {
      MinTracker<Tensor> minTracker = NegTransitiveMinTracker.withSet(weakOrderComparator);
      RandomSample.stream(tensor).forEach(minTracker::digest);
      collection1 = minTracker.getMinElements();
      assertTrue(0 < collection1.size());
    }
    Collection<Tensor> collection2;
    {
      MinTracker<Tensor> minTracker = NegTransitiveMinTracker.withSet(weakOrderComparator);
      RandomSample.stream(tensor).forEach(minTracker::digest);
      collection2 = minTracker.getMinElements();
      assertTrue(0 < collection2.size());
    }
    assertTrue(collection1.containsAll(collection2));
    assertTrue(collection2.containsAll(collection1));
  }
}
