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
    assertEquals(IntegerLog2.ceiling(1), 0);
    assertEquals(IntegerLog2.ceiling(2), 1);
    assertEquals(IntegerLog2.ceiling(3), 2);
    assertEquals(IntegerLog2.ceiling(4), 2);
    assertEquals(IntegerLog2.ceiling(5), 3);
    assertEquals(IntegerLog2.ceiling(1024 + 123), 11);
  }

  public void testFailZero() {
    AssertFail.of(() -> IntegerLog2.ceiling(0));
  }

  public void testFailNegative() {
    AssertFail.of(() -> IntegerLog2.ceiling(-1));
  }
}
