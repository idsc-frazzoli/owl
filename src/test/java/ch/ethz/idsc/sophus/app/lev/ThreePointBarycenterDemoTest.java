// code by jph
package ch.ethz.idsc.sophus.app.lev;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class ThreePointBarycenterDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ThreePointBarycenterDemo());
  }
}
