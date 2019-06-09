// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.sca.win.BartlettWindow;
import junit.framework.TestCase;

public class WindowCenterSamplerTest extends TestCase {
  public void testMemo() {
    WindowCenterSampler.of(BartlettWindow.FUNCTION);
  }

  public void testFailNull() {
    try {
      WindowCenterSampler.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
