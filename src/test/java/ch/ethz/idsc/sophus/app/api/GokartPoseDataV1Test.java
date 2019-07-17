// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartPoseDataV1Test extends TestCase {
  public void testSampleRate() {
    assertEquals(GokartPoseDataV1.INSTANCE.getSampleRate(), Quantity.of(20, "s^-1"));
  }
}
