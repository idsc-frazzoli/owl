// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.IOException;
import java.util.stream.DoubleStream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.Spectrogram;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum ColoredNoiseSpectrogram {
  ;
  public static void main(String[] args) throws IOException {
    ColoredNoise coloredNoise = new ColoredNoise(1); // 1 == pink noise
    Tensor tensor = Tensor.of(DoubleStream.generate(coloredNoise::nextValue) //
        .limit(1024 * 4).mapToObj(DoubleScalar::of));
    Tensor image = Spectrogram.of(tensor, ColorDataGradients.VISIBLESPECTRUM);
    Export.of(HomeDirectory.Pictures(ColoredNoiseSpectrogram.class.getSimpleName() + ".png"), image);
  }
}
