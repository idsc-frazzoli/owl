// code by ynager, found on github
package ch.ethz.idsc.owl.data.img;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public enum ImageArea {
  ;
  /** for an image with dimensions 640x640 the function takes ~6[s]
   * 
   * @param bufferedImage
   * @return area with all non-black pixels as member */
  public static Area fromImage(BufferedImage bufferedImage) {
    Area area = new Area();
    for (int y = 0; y < bufferedImage.getHeight(); ++y)
      for (int x = 0; x < bufferedImage.getWidth(); ++x)
        if ((bufferedImage.getRGB(x, y) & 0xffffff) != 0) // ff000000 or ff000000
          area.add(new Area(new Rectangle(x, y, 1, 1)));
    return area;
  }

  /** @param image non-null
   * @param color
   * @param tolerance
   * @return */
  public static Area fromImage(BufferedImage image, Color color, int tolerance) {
    Area area = new Area();
    for (int x = 0; x < image.getWidth(); ++x) {
      for (int y = 0; y < image.getHeight(); ++y) {
        Color pixel = new Color(image.getRGB(x, y));
        // System.out.println(pixel);
        if (isIncluded(color, pixel, tolerance)) {
          Rectangle r = new Rectangle(x, y, 1, 1);
          area.add(new Area(r));
          // System.out.println("added "+x+" "+y);
        }
      }
    }
    return area;
  }

  private static boolean isIncluded(Color target, Color pixel, int tolerance) {
    int rT = target.getRed();
    int gT = target.getGreen();
    int bT = target.getBlue();
    int rP = pixel.getRed();
    int gP = pixel.getGreen();
    int bP = pixel.getBlue();
    return ((rP - tolerance <= rT) && (rT <= rP + tolerance) && //
        (gP - tolerance <= gT) && (gT <= gP + tolerance) && //
        (bP - tolerance <= bT) && (bT <= bP + tolerance));
  }
}
