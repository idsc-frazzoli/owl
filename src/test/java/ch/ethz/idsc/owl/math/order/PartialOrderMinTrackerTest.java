// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class PartialOrderMinTrackerTest extends TestCase {
  public void testDigestNotEmpty() {
    PartialComparator<Scalar> comparator = PartialOrder.comparator(Scalars::divides);
    PartialOrderMinTracker<Scalar> divisibility = new PartialOrderMinTracker<>(comparator);
    divisibility.digest(RealScalar.of(6));
    assertFalse(divisibility.getMinElements().isEmpty());
  }

  public void testDigestFunction() {
    PartialComparator<Scalar> comparator = PartialOrder.comparator(Scalars::divides);
    PartialOrderMinTracker<Scalar> divisibility = new PartialOrderMinTracker<>(comparator);
    divisibility.digest(RealScalar.of(10));
    divisibility.digest(RealScalar.of(2));
    divisibility.digest(RealScalar.of(3));
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(6));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(3)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(7)));
  }

  public void testDuplicateEntries() {
    PartialComparator<Scalar> comparator = PartialOrder.comparator(Scalars::divides);
    PartialOrderMinTracker<Scalar> divisibility = new PartialOrderMinTracker<>(comparator);
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(7));
    assertEquals(divisibility.getMinElements().size(), 1);
  }
}
