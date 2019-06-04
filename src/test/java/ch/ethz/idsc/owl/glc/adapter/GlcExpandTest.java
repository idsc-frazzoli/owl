// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import junit.framework.TestCase;

public class GlcExpandTest extends TestCase {
  public void testFailNull() {
    try {
      new GlcExpand(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
