// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class DeadzoneTest extends TestCase {
  public void testSimple() {
    Deadzone deadzone = Deadzone.of(-2, 1);
    assertEquals(deadzone.apply(RealScalar.of(3)), RealScalar.of(2));
    assertEquals(deadzone.apply(RealScalar.of(-3)), RealScalar.of(-1));
    assertEquals(deadzone.apply(RealScalar.of(-2)), RealScalar.of(0));
    assertEquals(deadzone.apply(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(deadzone.apply(RealScalar.of(0.5)), RealScalar.of(0));
    assertEquals(deadzone.apply(RealScalar.of(1.5)), RealScalar.of(0.5));
  }

  public void testFail() {
    try {
      Deadzone.of(0, -1);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
