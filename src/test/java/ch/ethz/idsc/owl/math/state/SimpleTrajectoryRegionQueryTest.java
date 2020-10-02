// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.owl.math.AssertFail;
import junit.framework.TestCase;

public class SimpleTrajectoryRegionQueryTest extends TestCase {
  public void testSimple() {
    AssertFail.of(()->
      new SimpleTrajectoryRegionQuery(null));
  }
}
