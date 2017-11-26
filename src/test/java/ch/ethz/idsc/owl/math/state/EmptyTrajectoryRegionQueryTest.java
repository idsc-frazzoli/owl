// code by jph
package ch.ethz.idsc.owl.math.state;

import junit.framework.TestCase;

public class EmptyTrajectoryRegionQueryTest extends TestCase {
  public void testSimple() {
    assertFalse(EmptyTrajectoryRegionQuery.INSTANCE.firstMember(null).isPresent());
    assertFalse(EmptyTrajectoryRegionQuery.INSTANCE.isMember(null));
  }
}
