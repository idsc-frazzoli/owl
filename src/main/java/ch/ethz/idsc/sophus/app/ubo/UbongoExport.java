// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.ext.HomeDirectory;

/* package */ enum UbongoExport {
  ;
  private static final File FILE = HomeDirectory.Pictures("ubongo_publish3");

  public static void single() throws IOException {
    for (UbongoPublish ubongoPublish : UbongoPublish.values()) {
      BufferedImage bufferedImage = new BufferedImage( //
          UbongoViewer.maxWidth(), //
          UbongoViewer.maxHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      UbongoViewer.draw(graphics, ubongoPublish);
      ImageIO.write(bufferedImage, "png", new File(FILE, ubongoPublish + ".png"));
    }
  }

  private static final int NUMEL = 2;
  private static final int SPACE = 50;

  public static void bulk() throws IOException {
    List<UbongoPublish> list = Arrays.asList(UbongoPublish.values());
    list = list.subList(UbongoPublish.SPIRAL_1.ordinal(), list.size());
    int height = UbongoViewer.maxHeight();
    int page = 0;
    for (int index = 0; index < list.size(); index += NUMEL) {
      BufferedImage bufferedImage = new BufferedImage( //
          UbongoViewer.maxWidth(), //
          UbongoViewer.maxHeight() * NUMEL + SPACE * (NUMEL - 1), BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      // ---
      int top = Math.min(NUMEL, list.size() - index);
      int piy = 0;
      for (int sub = 0; sub < top; ++sub) {
        UbongoViewer.draw((Graphics2D) graphics.create(0, piy, bufferedImage.getWidth(), height), list.get(index + sub));
        piy += height;
        piy += SPACE;
      }
      // ---
      ImageIO.write(bufferedImage, "png", new File(FILE, String.format("page%03d.png", page)));
      ++page;
    }
  }

  public static void main(String[] args) throws IOException {
    FILE.mkdir();
    // single();
    bulk();
  }
}
