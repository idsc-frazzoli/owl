// code by jph
package ch.ethz.idsc.sophus.app.misc;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class PolygonClipDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new PolygonClipDemo());
  }
}
