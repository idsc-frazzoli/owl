// code by jph
package ch.ethz.idsc.sophus.app.decim;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class BulkDecimationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BulkDecimationDemo());
  }
}
