// code by jph
package ch.ethz.idsc.sophus.app.filter;

import ch.ethz.idsc.sophus.opt.SmoothingKernel;
import junit.framework.TestCase;

public class GeodesicCenterSymLinkImageTest extends TestCase {
  public void testSmoothingKernel() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      for (int radius = 0; radius < 5; ++radius)
        GeodesicCenterSymLinkImage.of(smoothingKernel, radius);
  }
}
