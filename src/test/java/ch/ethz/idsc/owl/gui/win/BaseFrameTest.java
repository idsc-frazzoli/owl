// code by jph
package ch.ethz.idsc.owl.gui.win;

import junit.framework.TestCase;

public class BaseFrameTest extends TestCase {
  public void testQuick() {
    try {
      BaseFrame bf = new BaseFrame();
      bf.jFrame.setVisible(true);
      bf.offscreen();
      bf.configCoordinateOffset(2, 3);
      bf.close();
    } catch (Exception exception) {
      // ---
    }
  }
}
