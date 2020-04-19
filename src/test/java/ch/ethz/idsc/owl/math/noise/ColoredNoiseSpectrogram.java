// code by jph
package ch.ethz.idsc.owl.math.noise;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.Spectrogram;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

/* package */ enum ColoredNoiseSpectrogram {
  ;
  public static void main(String[] args) throws IOException {
    ColoredNoise coloredNoise = new ColoredNoise(1); // 1 == pink noise
    Tensor tensor = RandomVariate.of(coloredNoise, 1024 * 4);
    Tensor image = Spectrogram.of(tensor, ColorDataGradients.VISIBLESPECTRUM);
    Export.of(HomeDirectory.Pictures(ColoredNoiseSpectrogram.class.getSimpleName() + ".png"), image);
  }
}
