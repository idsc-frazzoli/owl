// code by jph
package ch.ethz.idsc.sophus.app.dubins;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class DubinsPathDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new DubinsPathDemo());
  }
}
