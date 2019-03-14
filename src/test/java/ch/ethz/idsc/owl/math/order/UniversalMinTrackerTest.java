// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class UniversalMinTrackerTest extends TestCase {
  public void testDigestNotEmpty() {
    UniversalComparator<Scalar> universalComparator = UniversalOrder.comparator(Scalars::divides);
    UniversalMinTracker<Scalar> divisibility = UniversalMinTracker.withList(universalComparator);
    divisibility.digest(RealScalar.of(6));
    assertFalse(divisibility.getMinElements().isEmpty());
  }

  public void testPartial() {
    UniversalComparator<Scalar> universalComparator = UniversalOrder.comparator(Scalars::divides);
    UniversalMinTracker<Scalar> divisibility = UniversalMinTracker.withList(universalComparator);
    divisibility.digest(RealScalar.of(10));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(10)));
    divisibility.digest(RealScalar.of(2));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertFalse(divisibility.getMinElements().contains(RealScalar.of(10)));
    divisibility.digest(RealScalar.of(3));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(3)));
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(6));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(3)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(7)));
    assertFalse(divisibility.getMinElements().contains(RealScalar.of(6)));
  }

  public void testTotal() {
    UniversalComparator<Scalar> universalComparator = UniversalOrder.comparator(Scalars::lessEquals);
    UniversalMinTracker<Scalar> lessEquals = UniversalMinTracker.withList(universalComparator);
    lessEquals.digest(RealScalar.of(10));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(10)));
    lessEquals.digest(RealScalar.of(2));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(2)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(10)));
    lessEquals.digest(RealScalar.of(3));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(2)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(3)));
    lessEquals.digest(RealScalar.of(7));
    lessEquals.digest(RealScalar.of(6));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(2)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(3)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(7)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(6)));
    assertTrue(lessEquals.getMinElements().size() == 1);
  }
}
