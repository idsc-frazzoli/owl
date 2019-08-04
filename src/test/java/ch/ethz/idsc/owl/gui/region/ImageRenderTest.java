// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ImageRenderTest extends TestCase {
  public void testRangeFail1() {
    try {
      ImageRender.range(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testRangeFail2() {
    try {
      ImageRender.scale(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testImageNullFail() {
    try {
      ImageRender.scale(null, Tensors.vector(1, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
