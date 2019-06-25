// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import junit.framework.TestCase;

public class Clothoid3DisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(Clothoid3Display.INSTANCE.geodesicInterface(), Clothoid3.INSTANCE);
  }
}
