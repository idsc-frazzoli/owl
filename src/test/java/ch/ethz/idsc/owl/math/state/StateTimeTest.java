// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.owl.math.AssertFail;
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

  @SuppressWarnings("unlikely-arg-type")
  public void testEquals() {
    StateTime s1 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    assertFalse(s1.equals(null));
    assertFalse(s1.equals(RealScalar.ONE));
  }

  public void testFail() {
    AssertFail.of(() -> new StateTime(Tensors.vector(1, 2), null));
    AssertFail.of(() -> new StateTime(null, RealScalar.ZERO));
  }
}
