// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid2;
import junit.framework.TestCase;

public class Clothoid2DisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(Clothoid2Display.INSTANCE.geodesicInterface(), Clothoid2.INSTANCE);
  }
}
