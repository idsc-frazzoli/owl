// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClipStrictPartialComparatorTest extends TestCase {
  public void testIncomparable() {
    StrictPartialComparison strictPartialComparison1 = ClipStrictPartialComparator.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(0, 1));
    StrictPartialComparison strictPartialComparison2 = ClipStrictPartialComparator.INSTANCE.compare(Clips.interval(-1, 1), Clips.interval(0, 2));
    StrictPartialComparison strictPartialComparison3 = ClipStrictPartialComparator.INSTANCE.compare(Clips.interval(0, 5), Clips.interval(2, 3));
    StrictPartialComparison strictPartialComparison4 = ClipStrictPartialComparator.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(1, 2));
    assertTrue(strictPartialComparison1.equals(StrictPartialComparison.INCOMPARABLE));
    assertTrue(strictPartialComparison2.equals(StrictPartialComparison.INCOMPARABLE));
    assertTrue(strictPartialComparison3.equals(StrictPartialComparison.INCOMPARABLE));
    assertTrue(strictPartialComparison4.equals(StrictPartialComparison.INCOMPARABLE));
  }

  public void testLessThan() {
    StrictPartialComparison strictPartialComparison = ClipStrictPartialComparator.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(2, 3));
    assertTrue(strictPartialComparison.equals(StrictPartialComparison.LESS_THAN));
  }

  public void testGreaterThan() {
    StrictPartialComparison strictPartialComparison = ClipStrictPartialComparator.INSTANCE.compare(Clips.interval(4, 6), Clips.interval(0, 1));
    assertTrue(strictPartialComparison.equals(StrictPartialComparison.GREATER_THAN));
  }

  public void testSimple() {
    StrictPartialComparison strictPartialComparison = //
        ClipStrictPartialComparator.INSTANCE.compare(Clips.interval(0, 1), Clips.interval(0, 1));
    assertEquals(strictPartialComparison, StrictPartialComparison.INCOMPARABLE);
  }
}
