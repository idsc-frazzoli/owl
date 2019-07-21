// code by jph
package ch.ethz.idsc.sophus.io.obj;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSingle() {
    String[] strings = StaticHelper.slash("12//");
    assertEquals(strings[0], "12");
    assertEquals(strings[1], "");
    assertEquals(strings[2], "");
  }

  public void testDual() {
    String[] strings = StaticHelper.slash("12//34");
    assertEquals(strings[0], "12");
    assertEquals(strings[1], "");
    assertEquals(strings[2], "34");
  }
}
