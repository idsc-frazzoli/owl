// code by ynager
package ch.ethz.idsc.owl.data.img;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public enum ImageAlpha {
  ;
  public static BufferedImage scale(BufferedImage image, float scale) {
    float[] scales;
    float[] offsets;
    switch (image.getType()) {
    case BufferedImage.TYPE_INT_ARGB:
      scales = new float[] { 1.0f, 1.0f, 1.0f, scale };
      offsets = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
      return new RescaleOp(scales, offsets, null).filter(image, null);
    case BufferedImage.TYPE_4BYTE_ABGR:
      scales = new float[] { scale, 1.0f, 1.0f, 1.0f };
      offsets = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
      return new RescaleOp(scales, offsets, null).filter(image, null);
    case BufferedImage.TYPE_BYTE_GRAY:
      return new RescaleOp(scale, 0.0f, null).filter(image, null);
    }
    return image;
  }
}
