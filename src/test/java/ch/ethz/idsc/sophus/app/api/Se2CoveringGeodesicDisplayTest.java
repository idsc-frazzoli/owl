// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import junit.framework.TestCase;

public class Se2CoveringGeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(Se2CoveringGeodesicDisplay.INSTANCE.lieGroup(), Se2CoveringGroup.INSTANCE);
  }
}
