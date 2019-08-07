// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.io.ImageFormat;

public enum Binarize {
  ;
  public static BufferedImage of(BufferedImage bufferedImage) {
    return ImageFormat.of(ImageRegions.grayscale(ImageFormat.from(bufferedImage)));
  }
}
