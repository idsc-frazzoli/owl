// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.io.ImageFormat;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Binarize.html">Binarize</a> */
public enum Binarize {
  ;
  /** @param bufferedImage
   * @return */
  // TODO JPH function transforms RGBA to grayscale, but not black and white!
  public static BufferedImage of(BufferedImage bufferedImage) {
    return ImageFormat.of(ImageRegions.grayscale(ImageFormat.from(bufferedImage)));
  }
}
