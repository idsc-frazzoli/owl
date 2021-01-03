// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2DeformationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S2DeformationDemo());
  }
}
