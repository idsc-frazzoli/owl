// code by jph
package ch.ethz.idsc.owl.math.region;

import junit.framework.TestCase;

public class EmptyRegionTest extends TestCase {
  public void testSimple() {
    assertFalse(Regions.emptyRegion().isMember(null));
  }
}
