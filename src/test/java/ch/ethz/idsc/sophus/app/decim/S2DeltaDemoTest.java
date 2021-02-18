// code by jph
package ch.ethz.idsc.sophus.app.decim;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2DeltaDemoTest extends TestCase {
  public void testSimpleV1() {
    AbstractDemoHelper.offscreen(new S2DeltaDemo());
  }
}
