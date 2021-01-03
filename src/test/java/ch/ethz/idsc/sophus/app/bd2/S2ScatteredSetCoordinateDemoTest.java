// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2ScatteredSetCoordinateDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new PlanarScatteredSetCoordinateDemo());
  }
}
