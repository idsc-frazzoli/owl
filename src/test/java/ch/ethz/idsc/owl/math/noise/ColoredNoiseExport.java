// code by jph
package ch.ethz.idsc.owl.math.noise;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum ColoredNoiseExport {
  ;
  public static void main(String[] args) throws IOException {
    for (Tensor _x : Subdivide.of(0, 2, 10)) {
      ColoredNoise coloredNoise = new ColoredNoise(_x.Get().number().doubleValue());
      Tensor tensor = RandomVariate.of(coloredNoise, 10000);
      Export.of(HomeDirectory.file("cn" + _x.map(Round._1) + ".csv.gz"), tensor);
    }
  }
}
