// code by ynager
package ch.ethz.idsc.owl.data.img;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public enum ImageAlpha {
  ;
  public static BufferedImage scale(BufferedImage image, double scale) {
    float[] scales = { 1.0f, 1.0f, 1.0f, (float) scale };
    float[] offsets = { 0.0f, 0.0f, 0.0f, 0.0f };
    return new RescaleOp(scales, offsets, null).filter(image, null);
  }
}
