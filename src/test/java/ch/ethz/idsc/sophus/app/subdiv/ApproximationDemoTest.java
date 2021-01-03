// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class ApproximationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ApproximationDemo(GokartPoseDataV2.RACING_DAY));
  }
}
