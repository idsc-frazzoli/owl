// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.sophus.app.sym.SymLinkImage;
import ch.ethz.idsc.sophus.app.sym.SymLinkImages;
import ch.ethz.idsc.sophus.gui.win.AbstractDemoHelper;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class GeodesicBSplineFunctionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicBSplineFunctionDemo());
  }

  public void testDeBoorRational() {
    Scalar parameter = RationalScalar.of(9, 4);
    SymLinkImage symLinkImage = SymLinkImages.symLinkImageGBSF(4, 20, parameter);
    BufferedImage bufferedImage = symLinkImage.bufferedImage();
    assertTrue(300 < bufferedImage.getWidth());
    assertTrue(200 < bufferedImage.getHeight());
  }

  public void testDeBoorDecimal() {
    Scalar parameter = RealScalar.of(5.1);
    SymLinkImage symLinkImage = SymLinkImages.symLinkImageGBSF(5, 20, parameter);
    BufferedImage bufferedImage = symLinkImage.bufferedImage();
    assertTrue(300 < bufferedImage.getWidth());
    assertTrue(200 < bufferedImage.getHeight());
  }
}
