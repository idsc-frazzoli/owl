// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import junit.framework.TestCase;

public class ApproximationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ApproximationDemo(GokartPoseDataV2.RACING_DAY));
  }
}
