// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class DivisibilityPartialComparatorTest extends TestCase {
  public void testSimple() {
    assertEquals(DivisibilityPartialComparator.INSTANCE.compare(RealScalar.of(1), RealScalar.of(6)), PartialComparison.LESS_THAN);
    assertEquals(DivisibilityPartialComparator.INSTANCE.compare(RealScalar.of(6), RealScalar.of(6)), PartialComparison.EQUALS);
    assertEquals(DivisibilityPartialComparator.INSTANCE.compare(RealScalar.of(6), RealScalar.of(2)), PartialComparison.GREATER_THAN);
    assertEquals(DivisibilityPartialComparator.INSTANCE.compare(RealScalar.of(6), RealScalar.of(7)), PartialComparison.INCOMPARABLE);
  }
}
