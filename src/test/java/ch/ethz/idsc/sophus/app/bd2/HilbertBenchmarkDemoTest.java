// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class HilbertBenchmarkDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HilbertBenchmarkDemo());
  }
}
