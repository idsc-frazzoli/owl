// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import ch.ethz.idsc.sophus.app.subdiv.SplitCurveSubdivisionDemo;
import junit.framework.TestCase;

public class SplitCurveSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SplitCurveSubdivisionDemo());
  }
}
