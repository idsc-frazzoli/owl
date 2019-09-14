// code by jph
package ch.ethz.idsc.sophus.crv.dubins;

import junit.framework.TestCase;

public class DubinsPathComparatorTest extends TestCase {
  public void testSimple() {
    assertNotNull(DubinsPathComparator.LENGTH);
    assertNotNull(DubinsPathComparator.TOTAL_CURVATURE);
  }
}
