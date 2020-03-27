// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import junit.framework.TestCase;

public class HermiteDatasetDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HermiteDatasetDemo(GokartPoseDataV2.RACING_DAY));
  }
}
