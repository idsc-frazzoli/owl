// code by jph
package ch.ethz.idsc.sophus.sym;

import ch.ethz.idsc.sophus.app.filter.GeodesicCenterFilterDemo;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import junit.framework.TestCase;

public class SymLinkImagesTest extends TestCase {
  public void testSmoothingKernel() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      for (int radius = 0; radius < 5; ++radius)
        GeodesicCenterFilterDemo.symLinkImage(smoothingKernel, radius);
  }
}
