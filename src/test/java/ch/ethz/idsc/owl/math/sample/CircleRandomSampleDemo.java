// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Put;

enum CircleRandomSampleDemo {
  ;
  public static void main(String[] args) throws Exception {
    CircleRandomSample circleSampler = //
        new CircleRandomSample(Tensors.vector(1, 1), RealScalar.of(2));
    Tensor matrix = RandomSample.of(circleSampler, 10000);
    Put.of(UserHome.file("samples.txt"), matrix);
  }
}
