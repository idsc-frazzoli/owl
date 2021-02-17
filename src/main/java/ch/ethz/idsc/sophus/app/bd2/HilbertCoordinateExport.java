// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

/* package */ enum HilbertCoordinateExport {
  ;
  public static void main(String[] args) throws IOException {
    for (int n = 2; n < 5; ++n) {
      System.out.println(n);
      Tensor sequence = HilbertBenchmarkDemo.unit(n);
      BufferedImage bufferedImage = HilbertLevelImage.of( //
          R2Display.INSTANCE, sequence, 60, ColorDataGradients.CLASSIC, 800);
      ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures(String.format("hc%d.png", n)));
    }
  }
}
