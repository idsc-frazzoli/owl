// code by jph
package ch.ethz.idsc.sophus.app.io;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartPoseDataV2Test extends TestCase {
  public void testSampleRate() {
    assertEquals(GokartPoseDataV2.INSTANCE.getSampleRate(), Quantity.of(50, "s^-1"));
  }

  public void testRacingLength() {
    assertTrue(18 <= GokartPoseDataV2.RACING_DAY.list().size());
  }

  public void testListUnmodifiable() {
    AssertFail.of(() -> GokartPoseDataV2.INSTANCE.list().clear());
    AssertFail.of(() -> GokartPoseDataV2.RACING_DAY.list().clear());
  }
}
