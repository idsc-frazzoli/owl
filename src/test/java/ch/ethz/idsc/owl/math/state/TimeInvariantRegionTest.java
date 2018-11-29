// code by jph
package ch.ethz.idsc.owl.math.state;

import junit.framework.TestCase;

public class TimeInvariantRegionTest extends TestCase {
  public void testFailNull() {
    try {
      new TimeInvariantRegion(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
