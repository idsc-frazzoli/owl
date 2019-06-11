// code by jph
package ch.ethz.idsc.sophus.app.util;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface BufferedImageSupplier {
  /** @return bufferedImage */
  BufferedImage bufferedImage();
}
