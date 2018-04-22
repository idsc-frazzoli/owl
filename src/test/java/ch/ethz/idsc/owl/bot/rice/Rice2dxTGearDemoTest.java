// code by jph
package ch.ethz.idsc.owl.bot.rice;

import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import junit.framework.TestCase;

public class Rice2dxTGearDemoTest extends TestCase {
  public void testSimple() throws InterruptedException {
    OwlyAnimationFrame owlyAnimationFrame = new Rice2dxTGearDemo().start();
    owlyAnimationFrame.jFrame.setVisible(true);
    Thread.sleep(100);
    owlyAnimationFrame.jFrame.setVisible(false);
  }
}
