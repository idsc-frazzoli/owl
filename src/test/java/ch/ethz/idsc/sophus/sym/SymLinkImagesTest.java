// code by jph
package ch.ethz.idsc.sophus.sym;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class SymLinkImagesTest extends TestCase {
  public void testDeBoorRational() {
    Scalar parameter = RationalScalar.of(9, 4);
    SymLinkImage symLinkImage = SymLinkImages.deBoor(4, 20, parameter);
    BufferedImage bufferedImage = symLinkImage.bufferedImage();
    assertTrue(300 < bufferedImage.getWidth());
    assertTrue(200 < bufferedImage.getHeight());
  }

  public void testSmoothingKernel() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      for (int radius = 0; radius < 5; ++radius)
        SymLinkImages.geodesicCenter(smoothingKernel, radius);
  }
}
