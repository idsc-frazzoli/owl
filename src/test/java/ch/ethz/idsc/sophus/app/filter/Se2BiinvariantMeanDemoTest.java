// code by jph
package ch.ethz.idsc.sophus.app.filter;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class Se2BiinvariantMeanDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2BiinvariantMeanDemo());
  }
}
