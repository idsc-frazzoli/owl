// code by jph
package ch.ethz.idsc.sophus.app.io;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.fft.Spectrogram;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.num.Series;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/** Example from Mathematica::Spectrogram:
 * Table[Cos[ i/4 + (i/20)^2], {i, 2000}] */
/* package */ enum SpectrogramWindowDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = Subdivide.of(0, 100, 2000).map(Series.of(Tensors.vector(0, 5, 1))).map(Cos.FUNCTION);
    File folder = HomeDirectory.Pictures(SpectrogramWindowDemo.class.getSimpleName());
    folder.mkdir();
    for (WindowFunctions windowFunctions : WindowFunctions.values()) {
      ScalarUnaryOperator scalarUnaryOperator = windowFunctions.get();
      Tensor image = Spectrogram.of(tensor, scalarUnaryOperator, ColorDataGradients.VISIBLESPECTRUM);
      Export.of(new File(folder, windowFunctions.name() + ".png"), ImageResize.nearest(image, 4));
    }
  }
}
