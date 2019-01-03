// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

/** creates an image with unicode characters drawn inside */
/* package */ class CharImage {
  public static CharImage fillBlack(Dimension dimension) {
    return new CharImage(dimension, Color.BLACK, Color.WHITE);
  }

  public static CharImage fillWhite(Dimension dimension) {
    return new CharImage(dimension, Color.WHITE, Color.BLACK);
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Graphics graphics;

  private CharImage(Dimension dimension, Color fill, Color draw) {
    bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_BYTE_GRAY);
    graphics = bufferedImage.getGraphics();
    graphics.setColor(fill);
    graphics.fillRect(0, 0, dimension.width, dimension.height);
    graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, dimension.height * 5 / 4));
    graphics.setColor(draw);
  }

  public void setFont(Font font) {
    graphics.setFont(font);
  }

  public void draw(char chr, Point point) {
    graphics.drawString(Character.toString(chr), point.x, point.y);
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }
}
