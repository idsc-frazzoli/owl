// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class CurveSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new CurveSubdivisionDemo());
  }
}
