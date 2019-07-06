// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClipIntersectTest extends TestCase {
  public void testSimple() {
    Clip clip = ClipIntersect.of(Clips.interval(2, 6), Clips.interval(3, 10));
    assertEquals(clip.min(), RealScalar.of(3));
    assertEquals(clip.max(), RealScalar.of(6));
  }

  public void testPoint() {
    Clip clip = ClipIntersect.of(Clips.interval(2, 6), Clips.interval(-3, 2));
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(2));
  }

  public void testFail() {
    try {
      ClipIntersect.of(Clips.interval(2, 3), Clips.interval(5, 10));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
