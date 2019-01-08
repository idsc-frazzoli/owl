// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.Se2Group;
import junit.framework.TestCase;

public class Se2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(Se2GeodesicDisplay.INSTANCE.lieGroup(), Se2Group.INSTANCE);
  }
}
