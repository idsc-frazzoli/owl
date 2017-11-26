// code by jph
package ch.ethz.idsc.owl.data;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.img.CharImage;

enum CharImageDemo {
  ;
  // demo
  public static void main(String[] args) throws IOException {
    CharImage charImage = CharImage.fillWhite(new Dimension(236, 180));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 400));
    charImage.draw('a', new Point(-20, 200));
    BufferedImage bufferedImage = charImage.bufferedImage();
    ImageIO.write(bufferedImage, "png", UserHome.Pictures("circdots.png"));
  }
}
