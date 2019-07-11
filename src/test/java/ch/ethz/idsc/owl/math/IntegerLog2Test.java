// code by jph
package ch.ethz.idsc.owl.math;

import junit.framework.TestCase;

public class IntegerLog2Test extends TestCase {
  public void testSimple() {
    assertEquals(IntegerLog2.of(1), 0);
    assertEquals(IntegerLog2.of(2), 1);
    assertEquals(IntegerLog2.of(3), 1);
    assertEquals(IntegerLog2.of(4), 2);
    assertEquals(IntegerLog2.of(5), 2);
    assertEquals(IntegerLog2.of(1024 + 123), 10);
  }

  public void testFailZero() {
    try {
      IntegerLog2.of(0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNegative() {
    try {
      IntegerLog2.of(-1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
