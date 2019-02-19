// code by jph
package ch.ethz.idsc.owl.math.order;

import java.util.List;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class TypePartialComparatorTest extends TestCase {
  public void testSimple() {
    assertFalse(Scalar.class.isAssignableFrom(Tensor.class));
    assertTrue(Tensor.class.isAssignableFrom(Scalar.class));
  }

  public void testCompare() {
    assertEquals(TypePartialComparator.INSTANCE.compare(Tensor.class, Scalar.class), PartialComparison.LESS_THAN);
    assertEquals(TypePartialComparator.INSTANCE.compare(Object.class, Scalar.class), PartialComparison.LESS_THAN);
    assertEquals(TypePartialComparator.INSTANCE.compare(Tensor.class, Object.class), PartialComparison.GREATER_THAN);
    assertEquals(TypePartialComparator.INSTANCE.compare(List.class, Object.class), PartialComparison.GREATER_THAN);
    assertEquals(TypePartialComparator.INSTANCE.compare(List.class, List.class), PartialComparison.EQUALS);
    assertEquals(TypePartialComparator.INSTANCE.compare(List.class, Tensor.class), PartialComparison.INCOMPARABLE);
  }

  private static void _check(PartialOrderMinTracker<Class<?>> partialOrderMinTracker) {
    assertTrue(partialOrderMinTracker.getMinElements().isEmpty());
    partialOrderMinTracker.digest(Tensor.class);
    assertTrue(partialOrderMinTracker.getMinElements().contains(Tensor.class));
    partialOrderMinTracker.digest(Tensor.class);
    assertTrue(partialOrderMinTracker.getMinElements().contains(Tensor.class));
    assertEquals(partialOrderMinTracker.getMinElements().size(), 1);
    partialOrderMinTracker.digest(List.class);
    assertTrue(partialOrderMinTracker.getMinElements().contains(Tensor.class));
    assertTrue(partialOrderMinTracker.getMinElements().contains(List.class));
    assertEquals(partialOrderMinTracker.getMinElements().size(), 2);
    partialOrderMinTracker.digest(Scalar.class);
    assertTrue(partialOrderMinTracker.getMinElements().contains(Tensor.class));
    assertTrue(partialOrderMinTracker.getMinElements().contains(List.class));
    assertEquals(partialOrderMinTracker.getMinElements().size(), 2);
    partialOrderMinTracker.digest(Object.class);
    assertFalse(partialOrderMinTracker.getMinElements().contains(Tensor.class));
    assertFalse(partialOrderMinTracker.getMinElements().contains(List.class));
    assertTrue(partialOrderMinTracker.getMinElements().contains(Object.class));
    assertEquals(partialOrderMinTracker.getMinElements().size(), 1);
  }

  public void testMinTracker() {
    _check(PartialOrderMinTracker.withSet(TypePartialComparator.INSTANCE));
    _check(PartialOrderMinTracker.withList(TypePartialComparator.INSTANCE));
  }
}
