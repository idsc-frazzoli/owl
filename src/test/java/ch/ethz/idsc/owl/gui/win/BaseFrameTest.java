// code by jph
package ch.ethz.idsc.owl.gui.win;

import junit.framework.TestCase;

public class BaseFrameTest extends TestCase {
  public void testQuick() {
    BaseFrame bf = new BaseFrame();
    bf.jFrame.setVisible(true);
    bf.offscreen();
    bf.geometricComponent.setOffset(2, 3);
    bf.close();
  }
}
