// code by jph
package ch.ethz.idsc.owl.math.region;

import junit.framework.TestCase;

public class InvertedRegionTest extends TestCase {
  public void testSimple() {
    assertTrue(new InvertedRegion<>(Regions.emptyRegion()).isMember(null));
    assertFalse(new InvertedRegion<>(Regions.completeRegion()).isMember(null));
  }
}
