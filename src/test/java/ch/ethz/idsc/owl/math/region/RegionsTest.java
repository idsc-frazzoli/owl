package ch.ethz.idsc.owl.math.region;

import junit.framework.TestCase;

public class RegionsTest extends TestCase {
  public void testSimple() {
    assertTrue(Regions.completeRegion().isMember(null));
    assertFalse(Regions.emptyRegion().isMember(null));
  }
}
