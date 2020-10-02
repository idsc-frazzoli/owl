// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.owl.math.AssertFail;
import junit.framework.TestCase;

public class TimeInvariantRegionTest extends TestCase {
  public void testFailNull() {
    AssertFail.of(()->
      new TimeInvariantRegion(null));
  }
}
