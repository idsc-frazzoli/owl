// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class LaneRiesenfeldComparisonDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new LaneRiesenfeldComparisonDemo());
  }
}
