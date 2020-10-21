// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.ext.HomeDirectory;

/* package */ enum UbongoExport {
  ;
  public static void main(String[] args) throws IOException {
    File folder = HomeDirectory.Pictures("ubongo_publish");
    folder.mkdir();
    for (UbongoPublish ubongoPublish : UbongoPublish.values()) {
      BufferedImage bufferedImage = new BufferedImage(600, 300, BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      UbongoViewer.draw(graphics, ubongoPublish);
      ImageIO.write(bufferedImage, "png", new File(folder, ubongoPublish + ".png"));
    }
  }
}
