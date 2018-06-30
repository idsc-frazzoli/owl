// code by jph
package ch.ethz.idsc.owl.math.state;

import junit.framework.TestCase;

public class SimpleTrajectoryRegionQueryTest extends TestCase {
  public void testSimple() {
    try {
      new SimpleTrajectoryRegionQuery(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
