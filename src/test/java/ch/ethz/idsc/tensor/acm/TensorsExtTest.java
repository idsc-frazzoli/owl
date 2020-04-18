// code by jph
package ch.ethz.idsc.tensor.acm;

import junit.framework.TestCase;

public class TensorsExtTest extends TestCase {
  public void testSimple() {
    try {
      TensorsExt.of("abc");
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
