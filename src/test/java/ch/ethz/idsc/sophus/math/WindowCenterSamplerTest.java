// code by jph
package ch.ethz.idsc.sophus.math;

import junit.framework.TestCase;

public class WindowCenterSamplerTest extends TestCase {
  public void testFailNull() {
    try {
      new WindowCenterSampler(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
