// code by jph
package ch.ethz.idsc.sophus.dubins;

import junit.framework.TestCase;

public class DubinsPathComparatorTest extends TestCase {
  public void testSimple() {
    assertNotNull(DubinsPathComparator.length());
    assertNotNull(DubinsPathComparator.curvature());
  }
}
