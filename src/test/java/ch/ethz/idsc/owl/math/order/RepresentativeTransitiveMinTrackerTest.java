// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.demo.order.DigitSumDivisibilityPreorder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class RepresentativeTransitiveMinTrackerTest extends TestCase {
  public void testDigestNotEmptyList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(123);
    assertFalse(minTracker.getMinElements().isEmpty());
  }

  public void testDigestNotEmptySet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    minTracker.digest(123);
    assertFalse(minTracker.getMinElements().isEmpty());
  }

  public void testWithList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(123);
    assertTrue(minTracker.getMinElements().contains(123));
    minTracker.digest(122);
    assertTrue(minTracker.getMinElements().contains(123));
    assertTrue(minTracker.getMinElements().contains(122));
    minTracker.digest(426);
    assertFalse(minTracker.getMinElements().contains(426));
    minTracker.digest(1);
    assertTrue(minTracker.getMinElements().contains(1));
    assertTrue(minTracker.getMinElements().size() == 1);
  }

  public void testWithSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(122);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    assertTrue(digitSumDivisibility.getMinElements().contains(122));
    digitSumDivisibility.digest(426);
    assertFalse(digitSumDivisibility.getMinElements().contains(426));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  public void testDuplicateEntriesList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(333);
    minTracker.digest(333);
    assertTrue(minTracker.getMinElements().contains(333));
    assertTrue(minTracker.getMinElements().size() == 1);
  }

  public void testDuplicateEntriesSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    minTracker.digest(333);
    minTracker.digest(333);
    assertTrue(minTracker.getMinElements().contains(333));
    assertTrue(minTracker.getMinElements().size() == 1);
  }

  public void testOnlyPreoneRepresentativeList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(123);
    assertTrue(minTracker.getMinElements().contains(123));
    minTracker.digest(213);
    assertFalse(minTracker.getMinElements().contains(213));
    minTracker.digest(443);
    assertTrue(minTracker.getMinElements().contains(443));
    minTracker.digest(1111223);
    assertFalse(minTracker.getMinElements().contains(1111223));
    minTracker.digest(1);
    assertTrue(minTracker.getMinElements().contains(1));
    assertTrue(minTracker.getMinElements().size() == 1);
  }

  public void testOnlyPreoneRepresentativeSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withSet(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(213);
    assertFalse(digitSumDivisibility.getMinElements().contains(213));
    digitSumDivisibility.digest(443);
    assertTrue(digitSumDivisibility.getMinElements().contains(443));
    digitSumDivisibility.digest(1111223);
    assertFalse(digitSumDivisibility.getMinElements().contains(1111223));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  private static void _checkPermutations(Supplier<MinTracker<Scalar>> supplier) {
    Distribution distribution = DiscreteUniformDistribution.of(1, 10000);
    Tensor tensor = RandomVariate.of(distribution, 100);
    List<Tensor> list = Unprotect.list(tensor.copy());
    Collection<Scalar> collection1;
    {
      Collections.shuffle(list);
      MinTracker<Scalar> minTracker = supplier.get();
      list.stream().map(Scalar.class::cast).forEach(minTracker::digest);
      collection1 = minTracker.getMinElements();
      assertTrue(0 < collection1.size());
    }
    Collection<Scalar> collection2;
    {
      Collections.shuffle(list);
      MinTracker<Scalar> minTracker = supplier.get();
      list.stream().map(Scalar.class::cast).forEach(minTracker::digest);
      collection2 = minTracker.getMinElements();
      assertTrue(0 < collection2.size());
    }
    assertEquals(collection1.size(), collection2.size());
  }

  public void testPermutations() {
    _checkPermutations(() -> RepresentativeTransitiveMinTracker.withSet(DigitSumDivisibilityPreorder.SCALAR));
    _checkPermutations(() -> RepresentativeTransitiveMinTracker.withList(DigitSumDivisibilityPreorder.SCALAR));
  }
}
