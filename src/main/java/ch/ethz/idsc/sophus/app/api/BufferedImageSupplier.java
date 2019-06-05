// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface BufferedImageSupplier {
  /** @return bufferedImage */
  BufferedImage bufferedImage();
}
