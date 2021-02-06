// code by jph
package ch.ethz.idsc.sophus.app.filter;

import ch.ethz.idsc.sophus.app.sym.SymLinkImages;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class GeodesicCenterSymLinkImageTest extends TestCase {
  public void testSmoothingKernel() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values())
      for (int radius = 0; radius < 5; ++radius)
        SymLinkImages.ofGC(smoothingKernel.get(), radius);
  }
}
