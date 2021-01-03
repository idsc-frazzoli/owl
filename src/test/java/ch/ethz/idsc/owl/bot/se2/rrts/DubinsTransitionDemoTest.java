// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class DubinsTransitionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new DubinsTransitionDemo());
  }
}
