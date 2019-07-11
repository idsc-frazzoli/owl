// code by jph
package ch.ethz.idsc.owl.math;

import junit.framework.TestCase;

public class IntegerLog2Test extends TestCase {
  public void testFloor() {
    assertEquals(IntegerLog2.floor(1), 0);
    assertEquals(IntegerLog2.floor(2), 1);
    assertEquals(IntegerLog2.floor(3), 1);
    assertEquals(IntegerLog2.floor(4), 2);
    assertEquals(IntegerLog2.floor(5), 2);
    assertEquals(IntegerLog2.floor(1024 + 123), 10);
  }

  public void testCeil() {
    assertEquals(IntegerLog2.ceil(1), 0);
    assertEquals(IntegerLog2.ceil(2), 1);
    assertEquals(IntegerLog2.ceil(3), 2);
    assertEquals(IntegerLog2.ceil(4), 2);
    assertEquals(IntegerLog2.ceil(5), 3);
    assertEquals(IntegerLog2.ceil(1024 + 123), 11);
  }

  public void testFailZero() {
    try {
      IntegerLog2.ceil(0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNegative() {
    try {
      IntegerLog2.ceil(-1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
