// code by gjoel
package ch.ethz.idsc.sophus.crd;

import ch.ethz.idsc.tensor.alg.Array;
import junit.framework.TestCase;

public class CoordinateSystemTest extends TestCase {
  private static final String TEST = "test";

  public void testName() {
    CoordinateSystem cs = CoordinateSystem.of(TEST);
    assertEquals(TEST, cs.name());
  }

  public void testOrigin() {
    CoordinateSystem cs = CoordinateSystem.of(TEST);
    assertEquals(Array.zeros(3), cs.origin().values());
  }

  public void testEquals() {
    CoordinateSystem cs1 = CoordinateSystem.of(TEST);
    CoordinateSystem cs2 = CoordinateSystem.of(TEST);
    assertEquals(cs1, cs2);
  }

  public void testFails() {
    CoordinateSystem cs = CoordinateSystem.of(TEST);
    assertFalse(cs.equals(CoordinateSystem.DEFAULT));
  }
}
