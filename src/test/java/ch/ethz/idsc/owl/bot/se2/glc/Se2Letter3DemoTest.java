// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import junit.framework.TestCase;

public class Se2Letter3DemoTest extends TestCase {
  public void testSimple() throws InterruptedException {
    OwlyAnimationFrame owlyAnimationFrame = new Se2Letter3Demo().start();
    owlyAnimationFrame.jFrame.setVisible(true);
    Thread.sleep(100);
    owlyAnimationFrame.jFrame.setVisible(false);
  }
}
