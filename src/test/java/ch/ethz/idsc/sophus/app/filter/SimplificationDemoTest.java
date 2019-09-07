// code by jph
package ch.ethz.idsc.sophus.app.filter;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV1;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import junit.framework.TestCase;

public class SimplificationDemoTest extends TestCase {
  public void testSimpleV1() {
    AbstractDemoHelper.brief(new CurveDecimationDemo(GokartPoseDataV1.INSTANCE));
  }

  public void testSimpleV2() {
    AbstractDemoHelper.brief(new CurveDecimationDemo(GokartPoseDataV2.INSTANCE));
  }
}
