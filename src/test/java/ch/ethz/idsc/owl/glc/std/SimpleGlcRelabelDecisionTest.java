// code by jph
package ch.ethz.idsc.owl.glc.std;

import junit.framework.TestCase;

public class SimpleGlcRelabelDecisionTest extends TestCase {
  private static void check(boolean a, boolean b, boolean c) {
    boolean r1 = a || (b && c);
    boolean r2 = a || b && c;
    assertEquals(r1, r2);
  }

  public void testSimple() {
    check(true, true, true);
    check(true, true, false);
    check(true, false, true);
    check(true, false, false);
    check(false, true, true);
    check(false, true, false);
    check(false, false, true);
    check(false, false, false);
  }
}
