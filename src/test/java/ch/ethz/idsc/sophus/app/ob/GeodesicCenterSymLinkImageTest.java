// code by jph
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import junit.framework.TestCase;

public class GeodesicCenterSymLinkImageTest extends TestCase {
  public void testSmoothingKernel() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      for (int radius = 0; radius < 5; ++radius)
        GeodesicCenterSymLinkImage.of(smoothingKernel, radius);
  }
}
