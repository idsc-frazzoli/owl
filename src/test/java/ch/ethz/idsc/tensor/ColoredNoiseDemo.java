// code by jph
package ch.ethz.idsc.tensor;

import java.io.IOException;
import java.util.stream.DoubleStream;

import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Round;

public enum ColoredNoiseDemo {
  ;
  public static void main(String[] args) throws IOException {
    for (Tensor _x : Subdivide.of(0, 2, 10)) {
      ColoredNoise coloredNoise = new ColoredNoise(_x.Get().number().doubleValue());
      Tensor tensor = Tensor.of(DoubleStream.generate(coloredNoise::nextValue) //
          .limit(10000).mapToObj(DoubleScalar::of));
      Export.of(HomeDirectory.file("cn" + _x.map(Round._1) + ".csv.gz"), tensor);
    }
  }
}
