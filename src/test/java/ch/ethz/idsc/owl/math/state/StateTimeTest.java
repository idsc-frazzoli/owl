// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StateTimeTest extends TestCase {
  public void testSimple() {
    StateTime s1 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    StateTime s2 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    assertEquals(s1, s2);
    assertEquals(s1.hashCode(), s2.hashCode());
  }

  public void testNotEquals() {
    StateTime s1 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    StateTime s2 = new StateTime(Tensors.vector(1, 2, 1), RealScalar.of(2));
    StateTime s3 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(3));
    assertFalse(s1.equals(s2));
    assertFalse(s1.equals(s3));
  }

  public void testEquals() {
    StateTime s1 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    assertFalse(s1.equals(null));
    assertFalse(s1.equals(RealScalar.ONE));
  }

  public void testFail() {
    try {
      new StateTime(Tensors.vector(1, 2), null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      new StateTime(null, RealScalar.ZERO);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
