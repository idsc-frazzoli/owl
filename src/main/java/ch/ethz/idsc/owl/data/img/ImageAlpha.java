// code by ynager
package ch.ethz.idsc.owl.data.img;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public enum ImageAlpha {
  ;
  private static final float[] ZEROS = new float[] { 0, 0, 0, 0 };

  public static BufferedImage scale(BufferedImage bufferedImage, float scale) {
    float[] scales;
    switch (bufferedImage.getType()) {
    case BufferedImage.TYPE_INT_ARGB: // used by the tensor library
      scales = new float[] { 1, 1, 1, scale };
      return new RescaleOp(scales, ZEROS, null).filter(bufferedImage, null);
    case BufferedImage.TYPE_4BYTE_ABGR:
      scales = new float[] { scale, 1, 1, 1 };
      return new RescaleOp(scales, ZEROS, null).filter(bufferedImage, null);
    default:
      throw new RuntimeException();
    }
  }

  public static BufferedImage grayscale(BufferedImage image, float scale) {
    switch (image.getType()) {
    case BufferedImage.TYPE_BYTE_GRAY:
      return new RescaleOp(scale, (1 - scale) * 256, null).filter(image, null);
    default:
      throw new RuntimeException();
    }
  }
}
