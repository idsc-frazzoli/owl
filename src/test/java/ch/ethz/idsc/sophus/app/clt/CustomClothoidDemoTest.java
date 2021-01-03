// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class CustomClothoidDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new CustomClothoidDemo());
  }
}
