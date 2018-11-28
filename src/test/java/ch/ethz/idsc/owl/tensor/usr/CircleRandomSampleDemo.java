// code by jph
package ch.ethz.idsc.owl.tensor.usr;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.sample.SphereRandomSample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Put;

/** demo exports random samples from a circle that for visualization in Mathematica:
 * A = << "samples.txt"; ListPlot[A, AspectRatio -> 1] */
enum CircleRandomSampleDemo {
  ;
  public static void main(String[] args) throws Exception {
    RandomSampleInterface randomSampleInterface = //
        SphereRandomSample.of(Tensors.vector(1, 1), RealScalar.of(2));
    Tensor matrix = RandomSample.of(randomSampleInterface, 10000);
    Put.of(UserHome.file("samples.txt"), matrix);
  }
}
