// code by ynager
package ch.ethz.idsc.owl.data.img;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public enum ImageAlpha {
  ;
  private static final float[] ZEROS = new float[] { 0, 0, 0, 0 };

  public static BufferedImage scale(BufferedImage bufferedImage, float scale) {
    switch (bufferedImage.getType()) {
    case BufferedImage.TYPE_INT_ARGB: // used by the tensor library
      return new RescaleOp(new float[] { 1, 1, 1, scale }, ZEROS, null).filter(bufferedImage, null);
    case BufferedImage.TYPE_4BYTE_ABGR:
      return new RescaleOp(new float[] { scale, 1, 1, 1 }, ZEROS, null).filter(bufferedImage, null);
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
