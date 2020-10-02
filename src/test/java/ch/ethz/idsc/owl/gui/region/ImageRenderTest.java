// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ImageRenderTest extends TestCase {
  public void testRangeFail1() {
    AssertFail.of(() -> ImageRender.range(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3)));
  }

  public void testRangeFail2() {
    AssertFail.of(() -> ImageRender.scale(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3)));
  }

  public void testImageNullFail() {
    AssertFail.of(() -> ImageRender.scale(null, Tensors.vector(1, 2)));
  }
}
