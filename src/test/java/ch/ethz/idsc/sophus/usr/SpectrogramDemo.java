// code by jph
package ch.ethz.idsc.sophus.usr;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Spectrogram;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum SpectrogramDemo {
  ;
  public static void main(String[] args) throws IOException {
    // Example from Mathematica::Spectrogram
    // Table[Cos[ i/4 + (i/20)^2], {i, 2000}]
    Tensor tensor = Tensor.of(IntStream.range(0, 2000) //
        .mapToDouble(i -> Math.cos(i * 0.25 + (i / 20.0) * (i / 20.0))) //
        .mapToObj(RealScalar::of));
    Tensor spectrogram = Spectrogram.array(tensor);
    File folder = HomeDirectory.Pictures(SpectrogramDemo.class.getSimpleName());
    folder.mkdir();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Tensor image = ArrayPlot.of(spectrogram, colorDataGradients);
      Export.of(new File(folder, colorDataGradients.name() + ".png"), ImageResize.nearest(image, 4));
    }
  }
}
