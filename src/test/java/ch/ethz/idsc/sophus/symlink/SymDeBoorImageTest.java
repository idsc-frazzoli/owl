// code by jph
package ch.ethz.idsc.sophus.symlink;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.RationalScalar;
import junit.framework.TestCase;

public class SymDeBoorImageTest extends TestCase {
  public void testSimple() throws IOException {
    SymDeBoorImage symDeBoorImage = new SymDeBoorImage(5, 10, RationalScalar.of(2 + 3 * 4, 3));
    SymLinkImage symLinkImage = symDeBoorImage.symLinkImage;
    BufferedImage bufferedImage = symLinkImage.bufferedImage();
    ImageIO.write(bufferedImage, "png", UserHome.Pictures("deboor552a.png"));
  }
}
