// code by jph
package ch.ethz.idsc.sophus.usr;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.SpectrogramArray;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ enum SpectrogramDemo {
  ;
  public static void main(String[] args) throws IOException {
    // Example from Mathematica::Spectrogram
    // Table[Cos[ i/4 + (i/20)^2], {i, 2000}]
    Tensor tensor = Tensor.of(IntStream.range(0, 2000) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(RealScalar::of));
    Tensor spectrogram = SpectrogramArray.of(tensor).map(Abs.FUNCTION);
    int half = Unprotect.dimension1(spectrogram) / 2;
    Tensor spectrogram1 = Tensors.vector(i -> spectrogram.get(Tensor.ALL, half - i - 1), half);
    File folder = HomeDirectory.Pictures(SpectrogramDemo.class.getSimpleName());
    folder.mkdir();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Tensor image = ArrayPlot.of(spectrogram1, colorDataGradients);
      Export.of(new File(folder, colorDataGradients.name() + ".png"), ImageResize.nearest(image, 4));
    }
  }
}
