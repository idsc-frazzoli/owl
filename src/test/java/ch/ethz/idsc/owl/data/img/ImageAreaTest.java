// code by jph
package ch.ethz.idsc.owl.data.img;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

public class ImageAreaTest extends TestCase {
  public void testBlackWhite() throws IOException {
    BufferedImage bufferedImage = image("/dubilab/obstacles/20180423.png");
    Area area = ImageArea.fromImage(bufferedImage); // takes ~6[s]
    Rectangle rectangle = area.getBounds();
    assertEquals(rectangle.x, 16);
    assertEquals(rectangle.y, 16);
    assertEquals(rectangle.width, 593);
    assertEquals(rectangle.height, 585);
  }

  private static BufferedImage image(String string) throws IOException {
    try (InputStream inputStream = ImageAreaTest.class.getResourceAsStream(string)) { // auto closeable
      return ImageIO.read(inputStream);
    }
  }

  public void testFail() {
    try {
      ImageArea.fromImage(null, Color.BLACK, 2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
