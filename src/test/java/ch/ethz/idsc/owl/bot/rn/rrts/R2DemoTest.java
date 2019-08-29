// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import junit.framework.TestCase;

public class R2DemoTest extends TestCase {
  public void testSimple() throws InterruptedException {
    OwlyFrame owlyFrame = R2Demo.show();
    Thread.sleep(200);
    owlyFrame.jFrame.dispose();
  }
}
