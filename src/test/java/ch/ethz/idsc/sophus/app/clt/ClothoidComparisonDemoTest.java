// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class ClothoidComparisonDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidComparisonDemo());
  }
}
